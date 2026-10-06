package miniredis.store;

import java.util.Objects;

public class ExpiryCleaner implements AutoCloseable {

    private final Store<?, ?> store; // the store to clean on
    private final long intervalMillis; // the interval between two cleans
    private volatile boolean running = false;// we use volatile here because without it different threads read from
                                             // different cache
    private Thread cleaner;

    public ExpiryCleaner(Store<?, ?> store, long intervalMillis) {
        this.store = Objects.requireNonNull(store, "Store must not be null");
        this.intervalMillis = intervalMillis;
    }

    public void start() {
        if (running) // Stops you accidentally creating a second worker thread
            return;// shouldn't start again if the cleaner is already running
        running = true;
        cleaner = new Thread(this::loop, "expiry-cleaner");
        cleaner.start();
    }

    /* Method for the thread to invoke */
    private void loop() {
        while (running) {
            try {
                Thread.sleep(intervalMillis); // Thread.sleep(...) is inside loop(), which only runs on the cleaner, so
                                              // only cleaner can go to sleep
            } catch (InterruptedException e) {
                return;
            }
            clean();
        }
    }

    private void clean() {
        int removed = store.removeExpired();
        if (removed > 0) {
            System.out.println("The cleaner removed: " + removed + " expired keys.");
        }
    }

    @Override
    // In the try-resource block after it finish running close() is automatically
    // invoked
    public void close() {
        running = false;
        if (cleaner != null) {
            cleaner.interrupt(); // even if it is asleep it will wake up and InterruptedException will be
                                 // triggered in loop
            try {
                cleaner.join();// The main thread will wait for the cleaner to finsh closing, ensures cleaner
                               // is finished
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}