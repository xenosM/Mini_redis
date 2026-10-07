package miniredis.store;

import miniredis.testing.Assert;
import miniredis.testing.Test;

public class LfuPolicyTest {
    private final EvictionPolicy<String> policy = new LfuPolicy<>();
    @Test
    void emptyPolicyHasNoVictim(){
        Assert.assertFalse(policy.selectVictim().isPresent());
    }

    @Test
    void leastUsedKeyIsVictim(){
        policy.keyAccessed("A");
        policy.keyAccessed("A");
        policy.keyAccessed("A");
        policy.keyAccessed("B");
        policy.keyAccessed("C");
        policy.keyAccessed("C");
        //Key A and C are accessed more than Key B, so Key B will be the victim
        Assert.assertEquals("B",policy.selectVictim().orElse(null));
    }

    @Test
    void tieGoesToOldest() {
        policy.keyAccessed("B");
        policy.keyAccessed("B");
        policy.keyAccessed("A");
        policy.keyAccessed("A");

//        Key A and B are LFU keys but B is older than C, so B will be the victim
        Assert.assertEquals("B", policy.selectVictim().orElse(null));
    }

    @Test
    void tieAfterIncrementGoesToOldest(){
        policy.keyAccessed("A");
        policy.keyAccessed("B");
        policy.keyAccessed("A");
        policy.keyAccessed("B");
//        the most recently used is B and least frequently used is A, so A is removed.
        Assert.assertEquals("A", policy.selectVictim().orElse(null));
    }

    @Test
    void newKeyIsVictimOverPopularKey(){
        policy.keyAccessed("A");
        policy.keyAccessed("A");
        policy.keyAccessed("A");
        policy.keyAccessed("A");
        policy.keyAccessed("B");

        Assert.assertEquals("B", policy.selectVictim().orElse(null));
    }

    @Test
    void removedKeyIsNeverVictim(){
        policy.keyAccessed("A");
        policy.keyAccessed("B");
        policy.keyAccessed("B");
        policy.keyRemoved("A");

        Assert.assertEquals("B", policy.selectVictim().orElse(null));
    }

    @Test
    void victimsComeOutInOrderThenEmpty(){
        policy.keyAccessed("A");
        policy.keyAccessed("B");
        policy.keyAccessed("B");

        Assert.assertEquals("A", policy.selectVictim().orElse(null));
        Assert.assertEquals("B", policy.selectVictim().orElse(null));
        Assert.assertEquals(null, policy.selectVictim().orElse(null));
    }

}
