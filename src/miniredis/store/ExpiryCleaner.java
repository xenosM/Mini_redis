package miniredis.store;

import java.util.Objects;

public class ExpiryCleaner implements AutoCloseable {

    private final Store<?, ?> store; // the store to clean on
    private final long intervalMillis; // the interval between two cleans
    private volatile boolean running = false;// we use volatile here becuase without it different threads read from
                                             // different cache
    private Thread worker;

    public ExpiryCleaner(Store<?, ?> store, long intervalMillis) {
        this.store = Objects.requireNonNull(store, "Store must not be null");
        this.intervalMillis = intervalMillis;
    }

    public void start() {
        if (running)
            return;// shouldn't start again if the cleaner is already running
        running = true;
        worker = new Thread(this::loop, "expiry-cleaner");
        worker.start();
    }

    private void loop() {
        while (running) {
            try {
                Thread.sleep(intervalMillis);
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
    public void close() {
        running = false;
    }
}