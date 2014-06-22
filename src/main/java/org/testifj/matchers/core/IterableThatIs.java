package org.testifj.matchers.core;

import org.testifj.Matcher;

import java.util.Objects;

public final class IterableThatIs {

    public static <E, I extends Iterable<E>> Matcher<I> emptyIterable() {
        return iterable -> !iterable.iterator().hasNext();
    }

    @SafeVarargs
    public static <E, I extends Iterable<E>> Matcher<I> iterableOf(E... elements) {
        assert elements != null : "Elements can't be null";

        return iterable -> {
            int index = 0;

            for (E element : iterable) {
                if (index >= elements.length) {
                    return false;
                }

                if (!Objects.equals(element, elements[index++])) {
                    return false;
                }
            }

            return index == elements.length;
        };
    }

}
