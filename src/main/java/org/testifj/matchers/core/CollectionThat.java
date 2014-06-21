package org.testifj.matchers.core;

import org.testifj.Matcher;
import org.testifj.Predicate;

import java.util.Collection;

public final class CollectionThat {

    public static <E, T extends Collection<? extends E>> Matcher<T> containElement(Predicate<E> predicate) {
        assert predicate != null : "Predicate can't be null";

        return c -> {
            for (E element : c) {
                if (predicate.test(element)) {
                    return true;
                }
            }

            return false;
        };
    }
}
