package miniredis.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
This is just creating an annotation whose target is a method and will still be read during runtime
It is just a marker
The real work will be done by others, here by TestRunner.java
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

}