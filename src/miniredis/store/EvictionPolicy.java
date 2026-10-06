package miniredis.store;

import java.util.Optional;

public interface EvictionPolicy<K> {
    void keyAccessed(K key);

    void keyRemoved(K key);

    Optional<K> selectVictim();
}
