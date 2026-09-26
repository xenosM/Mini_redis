package miniredis.testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class TestRunner {
    private TestRunner() {
    }

    public static void run(Class<?> testClass) {
        System.out.println(testClass.getSimpleName());
        int passed = 0, failed = 0;
        for (Method method : testClass.getDeclaredMethods()) {

            // *If a method doesn't have the @Test annotation, it will be ignored */
            if (!method.isAnnotationPresent(Test.class)) {
                continue;
            }

            try {
                // * Setting up the instance for the parameter class */
                Constructor<?> constructor = testClass.getDeclaredConstructor();
                constructor.setAccessible(true); // Lets you run methods that are not public
                Object instance = constructor.newInstance();

                method.setAccessible(true);
                method.invoke(instance); // A non-static method needs an instance to run on

                System.out.println(" PASS: " + method.getName());
                passed++;

            } catch (InvocationTargetException e) { // a test method failed i.e. a test failed (NOT A LOGICAL ERROR)
                System.out.println(" FAIL: " + method.getName() + " -> " + e.getCause());
                failed++;
            } catch (ReflectiveOperationException e) { // Reflection itself went wrong, before or while trying to run
                                                       // the test
                System.out.println("  ERROR " + method.getName() + " -> couldn't run: " + e);
                failed++;
            }

        }
    }
}