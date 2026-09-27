package miniredis.store;

public interface Clock {
    long now(); // current time in millis
}
