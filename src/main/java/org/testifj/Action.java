package org.testifj;

@FunctionalInterface
public interface Action<T> {

    void execute(T instance);

    default Action<T> andThen() {
        return null;
    }

}
