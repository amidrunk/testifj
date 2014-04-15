package org.testifj;

@FunctionalInterface
public interface Describer<T> {

    Description describe(T instance);

}
