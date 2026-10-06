package miniredis.store;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class InMemoryStore<K, V> implements Store<K, V> {
    private final Clock clock;
    private final HashMap<K, Entry<V>> data = new HashMap<>();
    private final EvictionPolicy<K> policy;
    private final int maxSize;

    public InMemoryStore() {
        this(new SystemClock());
    }

    /* CONSTRUCTORS */
    public InMemoryStore(Clock clock) {
        //if not specified, size and policy will be the maxvalue of an integer and no policy by default;
        this(clock, Integer.MAX_VALUE, new NoEvictionPolicy<K>());
    }

    public InMemoryStore(Clock clock, int maxSize, EvictionPolicy<K> policy) {
        if (maxSize <= 0) throw new IllegalArgumentException("max size must be positive and non zero");
        this.maxSize = maxSize;
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /* PUBLIC METHODS*/
    @Override
    public synchronized void put(K key, V value) {
        Objects.requireNonNull(key, "Key must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        store(key, Entry.permanent(value));
    }

    @Override
    public synchronized void put(K key, V value, long ttlMillis) {
        Objects.requireNonNull(key, "Key must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        /*
         * ttlMillis represents the time left for the entry (eg. 5000ms) but what entry
         * expects is the time when entry should expire (i.e current time + time left)
         */
        store(key, new Entry<V>(value, clock.now() + ttlMillis));
    }

    @Override
    public synchronized Optional<V> get(K key) {
        Optional<Entry<V>> entry = liveEntry(key);
        if (entry.isPresent()) {
            policy.keyAccessed(key);
        }
        return liveEntry(key).map(Entry::value);
    }

    @Override
    public synchronized boolean delete(K key) {
        /* If a key is already expired, then removing it shouldn't return true */
        boolean existed = liveEntry(key).isPresent();
        if(existed){
            data.remove(key);
            policy.keyRemoved(key);
        }
        return existed;// only returns true when deleting a non-expired key
    }

    @Override
    public synchronized boolean exists(K key) {
        return liveEntry(key).isPresent();
    }

    @Override
    public synchronized int size() {
        removeExpired();
        return data.size();
    }

    @Override
    public synchronized boolean expire(K key, long ttlMillis) {
        Optional<Entry<V>> entryOptional = liveEntry(key);
        if (entryOptional.isPresent()) {
            this.put(key, entryOptional.get().value(), ttlMillis);
            return true;
        }
        return false;
    }

    @Override
    public synchronized long ttl(K key) {
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

    @Override
    public synchronized int removeExpired() {
        long now = clock.now();
        int before = data.size();
        data.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired(now)) {
                policy.keyRemoved(entry.getKey());
                return true;
            }
            return false;
        });
        return before - data.size();
    }

    /* PRIVATE METHODS */
    private synchronized Optional<Entry<V>> liveEntry(K key) {
        Entry<V> entry = data.get(key);

        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired(clock.now())) {
            data.remove(key);
            policy.keyRemoved(key);
            return Optional.empty();
        }
        return Optional.of(entry);
    }

    private void store(K key, Entry<V> entry) {
        boolean isNewKey = liveEntry(key).isEmpty();// if there is no live entry for a key then it is new
        if (isNewKey && data.size() >= maxSize) {
            makeRoom();
        }
        data.put(key, entry);
        policy.keyAccessed(key);
    }

    private void makeRoom() {
        removeExpired();
        //runs if there were no expired keys to remove
        while (data.size() >= maxSize) {
            Optional<K> victim = policy.selectVictim();
            // when the store is full and there is no policy set, the user cannot put new entry
            if (victim.isEmpty()) {
                throw new IllegalStateException("The store is full");
            }
            data.remove(victim.get());
        }
    }
}
