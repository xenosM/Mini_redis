package miniredis.store;

record Entry<V>(V value, long expiresAt) {

    static final long NO_EXPIRY = -1;

    public static <V> Entry<V> permanent(V value) { // factory method that returns a permanent entry
        return new Entry<V>(value, NO_EXPIRY);
    }

    boolean isExpired(long now) {
        // Returns true when entry is not permanent and current time is more or equals
        // to expiry time.
        return (expiresAt != NO_EXPIRY) && (now >= expiresAt);
    }

}
