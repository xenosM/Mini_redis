package miniredis.testing;

import java.lang.reflect.Method;

public final class TestRunner {
    private TestRunner() {
    }

    public static void run(Class<?> testClass) {
        System.out.println(testClass.getSimpleName());

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                System.out.println("  found: " + method.getName());
            }
        }
    }
}