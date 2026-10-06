package miniredis.store;

import miniredis.store.Clock;

class FakeClock implements Clock {
    private long time = 0;

    @Override
    public long now() {
        return time;
    }

    void advance(long millis) {
        time += millis;
    }

}