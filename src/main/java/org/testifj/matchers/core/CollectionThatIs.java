package org.testifj.matchers.core;

import org.testifj.Matcher;
import org.testifj.annotations.DSL;

import java.util.Collection;

@DSL
public final class CollectionThatIs {

    public static <E, C extends Collection<E>> Matcher<C> empty() {
        return instance -> instance != null && instance.isEmpty();
    }

}
