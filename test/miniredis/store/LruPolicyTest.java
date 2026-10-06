package miniredis.store;

import miniredis.testing.Assert;
import miniredis.testing.Test;

public class LruPolicyTest {
    private final EvictionPolicy<String> policy = new LruPolicy<>();

    @Test
    void emptyPolicyHasNoVictim() {
        Assert.assertFalse(policy.selectVictim().isPresent());
    }

    @Test
    void oldestUntouchedKeyIsVictim() {
        policy.keyAccessed("A");
        policy.keyAccessed("B");
        policy.keyAccessed("C");
        Assert.assertEquals("A", policy.selectVictim().orElse(null));
    }

    @Test
    void accessingKeyMakesItMostRecent() {
        policy.keyAccessed("A");
        policy.keyAccessed("B");
        policy.keyAccessed("C");
        policy.keyAccessed("A");
        Assert.assertEquals("B", policy.selectVictim().orElse(null));
    }

    @Test
    void removedKeyIsNeverVictim() {
        policy.keyAccessed("A");
        policy.keyAccessed("B");
        policy.keyRemoved("A");
        Assert.assertEquals("B", policy.selectVictim().orElse(null));
    }
}
