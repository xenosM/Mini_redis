package miniredis;

import miniredis.testing.TestRunner;
import miniredis.store.InMemoryStoreTest;

public class AllTests {
    public static void main(String[] args) {
        TestRunner.run(InMemoryStoreTest.class);
    }

}