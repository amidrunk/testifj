package org.testifj.matchers.core;

import org.testifj.Matcher;
import org.testifj.annotations.DSL;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

@DSL
public final class CollectionThatIs {

    public static <E, C extends Collection<E>> Matcher<C> empty() {
        return instance -> instance != null && instance.isEmpty();
    }

    public static <E, C extends Collection<E>> Matcher<C> ofSize(int size) {
        assert size >= 0 : "Size must be positive";
        return collection -> collection != null && collection.size() == size;
    }

    @SafeVarargs
    public static <E, T extends Collection<? extends E>> Matcher<T> collectionWithElements(Matcher<E>... matchers) {
        assert matchers != null : "Matchers can't be null";

        return instance -> {
            if (instance == null) {
                return false;
            }

            if (instance.size() != matchers.length) {
                return false;
            }

            int index = 0;

            for (E element : instance) {
                if (!matchers[index++].matches(element)) {
                    return false;
                }
            }

            return true;
        };
    }

    @SafeVarargs
    public static <E, C extends Collection<? extends E>> Matcher<C> collectionOf(E... elements) {
        assert elements != null : "Elements can't be null";

        return instance -> {
            if (instance.size() != elements.length) {
                return false;
            }

            int i = 0;

            for (Iterator<? extends E> iterator = instance.iterator(); iterator.hasNext(); i++) {
                if (!Objects.equals(iterator.next(), elements[i])) {
                    return false;
                }
            }

            return true;
        };
    }

}
