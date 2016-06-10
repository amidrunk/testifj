package org.testifj;

public class Expectations {

    public static<T> ObjectExpectations<T> expect(T instance) {
        return new ObjectExpectations<T>(instance);
    }
}
