package miniredis;

import miniredis.store.*;
import miniredis.testing.TestRunner;

public class AllTests {
    public static void main(String[] args) {
        TestRunner.run(InMemoryStoreTest.class);
        TestRunner.run(InMemoryStoreExpiryTest.class);
        TestRunner.run(LruPolicyTest.class);
        TestRunner.run(InMemoryStoreEvictionTest.class);
        TestRunner.run(CommandParserTest.class);
    }

}