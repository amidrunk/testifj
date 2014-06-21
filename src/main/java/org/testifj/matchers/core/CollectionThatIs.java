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

    @SafeVarargs
    public static <E, T extends Collection<? extends E>> Matcher<T> collectionOf(E... elements) {
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
