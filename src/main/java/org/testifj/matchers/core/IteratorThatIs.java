package org.testifj.matchers.core;

import org.testifj.Matcher;

import java.util.Iterator;
import java.util.Objects;

public final class IteratorThatIs {

    public static <T, I extends Iterator<T>> Matcher<I> emptyIterator() {
        return instance -> {
            if (instance == null) {
                return false;
            }

            return !instance.hasNext();
        };
    }

    @SafeVarargs
    public static <T, I extends Iterator<T>> Matcher<I> iteratorOf(T ... elements) {
        return instance -> {
            if (instance == null) {
                return false;
            }

            int index = 0;

            while (instance.hasNext()) {
                if (index >= elements.length) {
                    return false;
                }

                if (!Objects.equals(instance.next(), elements[index++])) {
                    return false;
                }
            }

            if (index != elements.length) {
                return false;
            }

            return true;
        };
    }

}
