package org.testifj;

public class ObjectExpectations<T> {

    private final T value;

    public ObjectExpectations(T value) {
        this.value = value;
    }

    public void toBeNull() {
    }
}
