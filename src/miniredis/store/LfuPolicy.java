package miniredis.store;

import java.util.*;

public class LfuPolicy<K> implements EvictionPolicy<K> {
    private final Map<K, Integer> keyCountMap = new HashMap<>();
    /*
      -> Bundles keys with the same count together
      -> Integer: Count, LinkedHashSet: Keys
    */
    private final TreeMap<Integer, LinkedHashSet<K>> countBucket = new TreeMap<>();

    /* PUBLIC METHODS */
    @Override
    public void keyAccessed(K key) {
        Integer prevCount = keyCountMap.get(key);
        // If the key is already in a bucket, then remove it from that bucket
        if (prevCount != null) {
            removeCountFromBucket(prevCount, key);
        }

        int newCount = prevCount == null ? 1 : prevCount + 1;
        keyCountMap.put(key, newCount); //updates the count for a key
        countBucket.computeIfAbsent(newCount, c -> new LinkedHashSet<>()).add(key);
    }

    @Override
    public void keyRemoved(K key) {
        Integer count = keyCountMap.remove(key);
        if (count != null) {
            removeCountFromBucket(count, key);
        }
    }

    @Override
    public Optional<K> selectVictim() {
        if (countBucket.isEmpty()) {
            return Optional.empty();
        }
        K victim = countBucket.firstEntry().getValue().getFirst();
        keyRemoved(victim);
        return Optional.of(victim);
    }

    /* PRIVATE METHODS */
    private void removeCountFromBucket(Integer prevCount, K key) {
        LinkedHashSet<K> bucket = countBucket.get(prevCount);
        bucket.remove(key);
        if (bucket.isEmpty()) {
            countBucket.remove(prevCount);
        }
    }

}
