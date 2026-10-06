package miniredis.store;

import miniredis.store.InMemoryStore;
import miniredis.store.Store;
import miniredis.testing.Test;
import miniredis.testing.Assert;

public class InMemoryStoreExpiryTest {
    private final FakeClock clock = new FakeClock();
    private final Store<String, String> store = new InMemoryStore<>(clock);

    @Test
    void keyExpiresExactlyAtTtl() {
        store.put("name", "mejal", 500);
        clock.advance(499);
        Assert.assertTrue(store.exists("name"));

        clock.advance(1);
        Assert.assertFalse(store.exists("name"));
    }

    @Test
    void ttlReturnsTimeLeft() {
        store.put("Session123", "User123", 1000);
        clock.advance(500);
        Assert.assertEquals(500L, store.ttl("Session123")); // 500L because the value of ttl is long not int
    }

    @Test
    void ttlOfPermanentKeyIsNoExpiry() {
        store.put("AdminName", "Hari");
        Assert.assertEquals(store.NO_EXPIRY, store.ttl("AdminName"));
    }

    @Test
    void ttlOfMissingKeyIsNoKey() {
        Assert.assertEquals(store.NO_KEY, store.ttl("notExist"));
    }

    @Test
    void expireSetsTtlOnExistingKey() {
        store.put("SessionID", "123");
        Assert.assertTrue(store.expire("SessionID", 100));
        Assert.assertEquals(100L, store.ttl("SessionID"));
    }

    @Test
    void expireMissingKeyReturnsFalse() {
        Assert.assertFalse(store.expire("SessionID", 100));
    }

    @Test
    void putWithoutTtlRemovesOldExpiry() {
        store.put("name", "mejal", 1000);
        Assert.assertEquals(1000L, store.ttl("name"));

        store.put("name", "mejal");
        Assert.assertEquals(-1L, store.ttl("name"));
    }

    @Test
    void deleteExpiredKeyReturnsFalse() {
        store.put("temp", "129384", 100);
        clock.advance(100);
        Assert.assertFalse(store.delete("temp"));
    }

    @Test
    void sizeIgnoresExpiredKeys() {
        store.put("key1", "v1", 100);
        store.put("key2", "v2", 200);
        store.put("key3", "v3", 300);
        clock.advance(100);

        Assert.assertEquals(2, store.size());
    }

    @Test
    void removeExpiredReturnsNumberRemoved() {
        store.put("key1", "v1", 100);
        store.put("key2", "v2", 200);
        store.put("key3", "v3", 300);
        clock.advance(200);

        Assert.assertEquals(2, store.removeExpired());
        Assert.assertEquals(1, store.size());
    }
}