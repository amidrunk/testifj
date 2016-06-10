package org.testifj.matchers.core;

import org.testifj.Matcher;
import io.recode.annotations.DSL;

@DSL
public class NumberThatIs {

    public static<T extends Number & Comparable<T>> Matcher<T> greaterThan(T other) {
        return value -> value != null && value.compareTo(other) > 0;
    }

    public static<T extends Number & Comparable<T>> Matcher<T> atLeast(T other) {
        return value -> value != null && value.compareTo(other) >= 0;
    }

    public static<T extends Number & Comparable<T>> Matcher<T> lessThan(T other) {
        return value -> value != null && value.compareTo(other) < 0;
    }

    public static<T extends Number & Comparable<T>> Matcher<T> atMost(T other) {
        return value -> value != null && value.compareTo(other) <= 0;
    }

    public static<T extends Number & Comparable<T>> Matcher<T> between(T min, T max) {
        return value -> value != null && value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
}
