package org.testifj;

@FunctionalInterface
public interface Describer<T> {

    String describe(T instance);

}
