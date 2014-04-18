package org.testifj;

@FunctionalInterface
public interface Action<T> {

    void execute(T instance) throws Exception;

    default Action<T> andThen() {
        return null;
    }

}
