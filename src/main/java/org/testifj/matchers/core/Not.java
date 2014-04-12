package org.testifj.matchers.core;

import org.testifj.Matcher;

public final class Not {

    public static <T> Matcher<T> not(Matcher<T> matcher) {
        return t -> !matcher.matches(t);
    }

}
