package miniredis.store;

import miniredis.testing.Assert;
import miniredis.testing.Test;

public class InMemoryStoreEvictionTest {
    private final FakeClock clock = new FakeClock();
    private final Store<String, String> store = new InMemoryStore<>(clock, 3, new LruPolicy<String>());

    @Test
    void evictsLeastRecentlyUsedWhenFull() {
        store.put("A", "1");
        store.put("B", "2");
        store.put("C", "3");
        store.put("D", "4");

        Assert.assertFalse(store.exists("A"));
        Assert.assertTrue(store.exists("D"));
        Assert.assertEquals(3, store.size());
    }

    @Test
    void getCountsAsUse() {
        store.put("A", "1");
        store.put("B", "2");
        store.put("C", "3");
        store.get("A");
        store.put("D", "4");
        Assert.assertFalse(store.exists("B"));
        Assert.assertTrue(store.exists("A"));
    }

    @Test
    void updatingExistingKeyDoesNotEvict() {
        store.put("A", "1");
        store.put("B", "2");
        store.put("C", "3");
        store.put("B", "4"); //Updating existing key

        Assert.assertTrue(store.exists("B"));
        Assert.assertEquals("4",store.get("B").orElse(null));
        Assert.assertEquals(3, store.size());
    }

    @Test
    void expiredKeysAreRemovedBeforeEvicting() {
        store.put("A", "1");
        store.put("B", "2");
        store.put("C", "3", 300);
        clock.advance(400);
        store.put("D", "4");
        /*
         * A is lru, but C is expired, and expired keys are removed first before lru keys are removed
         * So here instead of A, C is removed
         */
        Assert.assertFalse(store.exists("C"));
        Assert.assertTrue(store.exists("A"));
        Assert.assertTrue(store.exists("B"));
        Assert.assertTrue(store.exists("D"));
    }

    @Test
    void deletedKeyFreesSpace() {
        store.put("A", "1");
        store.put("B", "2");
        store.put("C", "3");
        store.delete("B");
        store.put("D", "4");
        Assert.assertTrue(store.exists("A"));// lru still exists
        Assert.assertTrue(store.exists("D"));
        Assert.assertFalse(store.exists("B"));// deleted key doesn't exist
        Assert.assertEquals(3, store.size());
    }

    @Test
    void getMissingKeyDoesNotBreakEviction() {
        TestPolicy<String> testPolicy = new TestPolicy<>();
        Store<String, String> testStore = new InMemoryStore<>(clock, 3, testPolicy);
        testStore.get("X");

        Assert.assertFalse(testPolicy.accessedKeys.contains("X"));
    }

    @Test
    void noEvictionPolicyThrowsWhenFull() {
        Store<String, String> testStore = new InMemoryStore<>(clock, 3, new NoEvictionPolicy<>());
        testStore.put("A", "1");
        testStore.put("B", "2");
        testStore.put("C", "3");
        try {
            testStore.put("D", "4");
            Assert.fail("Did not throw exception");
        } catch (IllegalStateException e) {
            return;
        }

    }

}
