package miniredis.store;

import java.util.Optional;

public interface Store<K, V> {

    void put(K key, V value);

    Optional<V> get(K key); // Used optional here, as it provides more safety and clarity when a value is
                            // not found. Rather than dealing with null, dealing with a Optional wrapper is
                            // easier and convinient

    boolean delete(K key);

    boolean exists(K key);

    int size();
}
