package org.testifj;

@FunctionalInterface
public interface Predicate<T> {

    boolean test(T instance);

    default String describe() {
        return "aPredicate";
    }

}
