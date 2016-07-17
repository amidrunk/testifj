package org.testifj.matchers.core;

import org.testifj.Matcher;

import java.util.Objects;

public class BooleanThatIs {

    public static Matcher<Boolean> equalToTrue() {
        return value -> Objects.equals(value, true);
    }

    public static Matcher<Boolean> equalToFalse() {
        return value -> Objects.equals(value, false);
    }
}
