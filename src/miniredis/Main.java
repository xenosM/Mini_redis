package miniredis;

import miniredis.store.ExpiryCleaner;
import miniredis.store.InMemoryStore;

public class Main {
    final static long CLEARNER_INTERVAL = 1000;

    public static void main(String[] args) throws InterruptedException {
        InMemoryStore<String, String> store = new InMemoryStore<>();
        try (ExpiryCleaner cleaner = new ExpiryCleaner(store, CLEARNER_INTERVAL)) {
            cleaner.start();
            store.put("code", "1234", 300); // expires after 300ms
            store.put("session", "user42", 600); // expires after 600ms
            store.put("name", "mejal"); // never expires

            Thread.sleep(2000); // main waits while the cleaner works

            System.out.println("Keys left: " + store.size());
        }

    }
}
