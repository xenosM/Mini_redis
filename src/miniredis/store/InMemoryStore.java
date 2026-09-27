package miniredis.store;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class InMemoryStore<K, V> implements Store<K, V> {
    private final Clock clock;
    private final HashMap<K, Entry<V>> data = new HashMap<>();

    public InMemoryStore() {
        this(new SystemClock());
    }

    public InMemoryStore(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    private Optional<Entry<V>> liveEntry(K key) {
        Entry<V> entry = data.get(key);

        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired(clock.now())) {
            data.remove(key);
            return Optional.empty();
        }
        return Optional.of(entry);
    }

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key, "Key must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        data.put(key, Entry.permanent(value));
    }

    @Override
    public void put(K key, V value, long ttlMillis) {
        Objects.requireNonNull(key, "Key must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        /*
         * ttlMillis represents the time left for the entry (eg. 5000ms) but what entry
         * expects is the time when entry should expire (i.e current time + time left)
         */
        data.put(key, new Entry<V>(value, clock.now() + ttlMillis));
    }

    @Override
    public Optional<V> get(K key) {
        return liveEntry(key).map(Entry::value);
    }

    @Override
    public boolean delete(K key) {
        /* If a key is already expired, then removing it shouldn't return true */
        boolean existed = liveEntry(key).isPresent();
        data.remove(key);
        return existed;// only returns true when deleting a non-expired key
    }

    @Override
    public boolean exists(K key) {
        return liveEntry(key).isPresent();
    }

    @Override
    public int size() {
        long now = clock.now();
        data.values().removeIf(entry -> entry.isExpired(now));
        return data.size();
    }

    @Override
    public boolean expire(K key, long ttlMillis) {
        Optional<Entry<V>> entryOptional = liveEntry(key);
        if (entryOptional.isPresent()) {
            this.put(key, entryOptional.get().value(), ttlMillis);
            return true;
        }
        return false;
    }

    @Override
    public long ttl(K key) {
        Entry<V> entry = liveEntry(key).orElse(null);
        if (entry == null) {
            return NO_KEY;
        }
        /*
         * entry.expiresAt() gives the time when the entry expires and not the time left
         * for the entry(which is what we actually nees), and so to get this we need to
         * do [entry.expiresAt() - clock.now()]
         */
        return entry.expiresAt() == Entry.NO_EXPIRY ? NO_EXPIRY : entry.expiresAt() - clock.now();
    }

}
