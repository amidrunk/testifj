package org.testifj;

@FunctionalInterface
public interface Matcher<T> {

    boolean matches(T instance);

}
