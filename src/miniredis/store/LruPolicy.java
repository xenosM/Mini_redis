package miniredis.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LruPolicy<K> implements EvictionPolicy<K> {
    private class Node {
        K key;
        Node next;
        Node prev;

        Node(K key) {
            this.key = key;
        }
    }

    private Map<K, Node> map = new HashMap<>();
    private final Node lru, mru;

    public LruPolicy() {
        lru = new Node(null);
        mru = new Node(null);
        lru.next = mru;
        mru.prev = lru;
    }

    @Override
    public void keyAccessed(K key) {
        if (map.containsKey(key)) {
            unlink(key);
        } else {
            Node newNode = new Node(key);
            map.put(key, newNode);
        }
        insertAtEnd(key);
    }

    @Override
    public void keyRemoved(K key) {
        if (map.containsKey(key)) {
            unlink(key);
            map.remove(key);
        }
    }

    @Override
    public Optional<K> selectVictim() {
        K victimKey = lru.next.key;
        keyRemoved(victimKey);
        return Optional.ofNullable(victimKey);
    }

    private void unlink(K key) {
        Node node = map.get(key);
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertAtEnd(K key) {
        Node node = map.get(key);
        mru.prev.next = node;
        node.next = mru;
        node.prev = mru.prev;
        mru.prev = node;
    }
}
