package miniredis.store;

import java.util.Optional;

public interface Store<K, V> {
    // Constants in an interface are automatically public static final
    long NO_KEY = -2;
    long NO_EXPIRY = -1;

    void put(K key, V value);

    void put(K key, V value, long ttlMillis);

    /*
     * Used optional here, as it provides more safety and clarity when a value is
     * not found. Rather than dealing with null, dealing with a Optional wrapper is
     * easier and convinient
     */
    Optional<V> get(K key);

    boolean delete(K key);

    boolean exists(K key);

    int size();

    boolean expire(K key, long ttlMillis);

    long ttl(K key); // Time to Live: time left before the key expires
}
