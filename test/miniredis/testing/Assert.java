package miniredis.testing;

import java.util.Objects;

//final here means that no class can extend it
public final class Assert {
    private Assert() {
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }

    public static <T> void assertEquals(T expected, T actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but was true");
        }
    }

    public static void assertNull(Object value) {
        if (value != null) {
            throw new AssertionError("Expect value to be null");
        }
    }

    public static void assertNotNull(Object value) {
        if (value == null) {
            throw new AssertionError("Expected a value but was null");
        }
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }
}