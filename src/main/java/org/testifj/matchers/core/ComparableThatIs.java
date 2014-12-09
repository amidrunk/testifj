package org.testifj.matchers.core;

import org.testifj.Matcher;
import org.testifj.annotations.DSL;

@DSL
public final class ComparableThatIs {

    public static <T extends Comparable<T>> Matcher<T> comparableTo(T other) {
        assert other != null : "other can't be null";

        return instance -> instance != null && instance.compareTo(other) == 0;
    }

    public static <T extends Comparable<T>> Matcher<T> lessThan(T other) {
        assert other != null : "other can't be null";

        return instance -> instance != null && instance.compareTo(other) < 0;
    }

    public static <T extends Comparable<T>> Matcher<T> greaterThan(T other) {
        assert other != null : "other can't be null";

        return instance -> instance != null && instance.compareTo(other) > 0;
    }

}
