package org.testifj;

@FunctionalInterface
public interface Expectation<T> {

    void verify(T t);

    default Expectation<T> capture(final Capture<T> capture) {
        return t -> {
            capture.set(t);
            this.verify(t);
        };
    }

}
