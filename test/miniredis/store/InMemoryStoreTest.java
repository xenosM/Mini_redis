package miniredis.store;

import miniredis.store.InMemoryStore;
import miniredis.store.Store;
import miniredis.testing.Assert;
import miniredis.testing.Test;

public class InMemoryStoreTest {
    private final Store<String, String> store = new InMemoryStore<>();

    @Test
    void putThenGetReturnValue() {
        store.put("name", "mejal");
        Assert.assertEquals("mejal", store.get("name").orElse(null));// Returns an Optional wrapper and if the optional
                                                                     // wrapper wraps around null then value the
                                                                     // Optional returns will be null
    }

    @Test
    void getMissingKeyReturnsEmpty() {
        Assert.assertFalse(store.get("notThere").isPresent());
    }

    @Test
    void putNullKeyThrows() {
        try {
            store.put(null, "foo");
            Assert.fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected so test passes
        }
    }

    @Test
    void putOverwritesExistingValue() {
        store.put("school", "value1");
        store.put("school", "value2");
        Assert.assertEquals("value2", store.get("school").orElse(null));
        Assert.assertEquals(1, store.size());
    }

    @Test
    void deleteExistingKeyReturnsTrue() {
        store.put("key1", "value");
        Assert.assertTrue(store.delete("key1"));
        Assert.assertFalse(store.exists("key1"));
    }

    @Test
    void deleteMissingKeyReturnsFalse() {
        Assert.assertFalse(store.delete("non-existent"));
    }

    @Test
    void existsReturnsTrueOnlyForStoredKeys() {
        store.put("exists", "value");
        Assert.assertTrue(store.exists("exists"));
        Assert.assertFalse(store.exists("missing"));
    }

    @Test
    void sizeCountsStoredKeys() {
        store.put("key1", "value");
        store.put("key2", "value");
        store.put("key3", "value");
        Assert.assertEquals(3, store.size());
    }

    @Test
    void storeWorksWithLfuPolicy(){
        Store<String,String> lfuStore = new InMemoryStore<>(new FakeClock(),3,new LfuPolicy<>());
        lfuStore.put("key1", "value");
        lfuStore.put("key1", "value");
        lfuStore.put("key2", "value");
        lfuStore.put("key3", "value");
        lfuStore.get("key2");
        lfuStore.get("key2");
        lfuStore.put("key4", "value");

        // 1 => {key3}, 2=>{key1,key2}. adding key4 replaces key3
        Assert.assertFalse(lfuStore.exists("key3"));
        Assert.assertTrue(lfuStore.exists("key1"));
        Assert.assertTrue(lfuStore.exists("key2"));
        Assert.assertTrue(lfuStore.exists("key4"));
        Assert.assertEquals(3,lfuStore.size());
    }
}