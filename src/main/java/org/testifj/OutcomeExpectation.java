package org.testifj;

@FunctionalInterface
public interface OutcomeExpectation<T> {

    void verify(T t);

    default OutcomeExpectation<T> capture(final Capture<T> capture) {
        return t -> {
            capture.set(t);
            this.verify(t);
        };
    }

}
