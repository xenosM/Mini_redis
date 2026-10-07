package miniredis.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestPolicy<K> implements EvictionPolicy<K>{
    List<K> accessedKeys = new ArrayList<>();
    @Override
    public void keyAccessed(K key) {
        accessedKeys.add(key);
    }

    @Override
    public void keyRemoved(K key) {

    }

    @Override
    public Optional<K> selectVictim() {
        return Optional.empty();
    }
}
