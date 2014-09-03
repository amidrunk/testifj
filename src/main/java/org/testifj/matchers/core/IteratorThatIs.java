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

                final T actualElement = instance.next();
                final T expectedElement = elements[index++];

                if (!Objects.equals(actualElement, expectedElement)) {
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
