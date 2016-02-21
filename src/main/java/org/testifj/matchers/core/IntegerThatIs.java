package org.testifj.matchers.core;

import org.testifj.Matcher;

public class IntegerThatIs {

    public static Matcher<Integer> negative() {
        return value -> value != null && value < 0;
    }

    public static Matcher<Integer> positive() {
        return value -> value != null && value >= 0;
    }

    public static Matcher<Integer> natural() {
        return value -> value != null && value > 0;
    }

    public static Matcher<Integer> even() {
        return value -> value != null && value % 2 == 0;
    }

    public static Matcher<Integer> odd() {
        return value -> value != null && value != 0 && Math.abs(value) % 2 == 1;
    }

    public static Matcher<Integer> greaterThan(int minValue) {
        return value -> value != null && value > minValue;
    }

    public static Matcher<Integer> atLeast(int minValue) {
        return value -> value != null && value >= minValue;
    }

    public static Matcher<Integer> lessThan(int minValue) {
        return value -> value != null && value < minValue;
    }

    public static Matcher<Integer> atMost(int maxValue) {
        return value -> value != null && value <= maxValue;
    }

    public static Matcher<Integer> between(int min, int max) {
        return value -> value != null && value >= min && value <= max;
    }
}
