package org.testifj.matchers.core;

import org.testifj.Matcher;

public final class Any {

    public static <T> Matcher<T> any() {
        return instance -> true;
    }

}
