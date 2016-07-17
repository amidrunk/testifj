package org.testifj;

import org.testifj.framework.ExpectationContext;
import org.testifj.framework.ExpectationReference;
import org.testifj.framework.InlineExpectationReference;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Expectations {

    public static <T> OptionalExpectations<T> expect(Optional<T> optional) {
        return new OptionalExpectations<T>(createContext(optional));
    }

    public static <T> ArrayExpectations<T> expect(T[] array) {
        return new ArrayExpectations<T>(createContext(array));
    }

    public static IntegerExpectations expect(int value) {
        return new IntegerExpectations(createContext(value));
    }

    public static <T> ObjectExpectations<T> expect(T instance) {
        return new ObjectExpectations<T>(createContext(instance));
    }

    public static BehaviouralExpectations expect(Procedure procedure) {
        return new BehaviouralExpectations(createContext(procedure));
    }

    public static <T> ListExpectations<T> expect(List<T> list) {
        return new ListExpectations<>(createContext(list));
    }

    public static BooleanExpectations expect(boolean value) {
        return new BooleanExpectations(createContext(value));
    }

    public static <T> IteratorExpectations<T> expect(Iterator<T> iterator) {
        return new IteratorExpectations<T>(createContext(iterator));
    }

    public static <K, V> MapExpectations<K, V> expect(Map<K, V> map) {
        return new MapExpectations<K, V>(createContext(map));
    }

    private static <T> ExpectationContext<T> createContext(T subject) {
        final ExpectationReference reference = InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 3);
        return new ExpectationContext<>(subject, reference);
    }
}
