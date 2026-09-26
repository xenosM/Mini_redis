package miniredis.store;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class InMemoryStore<K, V> implements Store<K, V> {
    private final HashMap<K, V> data = new HashMap<>();

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key, "Key must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        data.put(key, value);
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(data.get(key));
    }

    @Override
    public boolean delete(K key) {
        return data.remove(key) != null;
    }

    @Override
    public boolean exists(K key) {
        return data.containsKey(key);
    }

    @Override
    public int size() {
        return data.size();
    }
}
