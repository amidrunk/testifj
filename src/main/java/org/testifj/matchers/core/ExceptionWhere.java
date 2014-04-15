package org.testifj.matchers.core;

import org.testifj.Matcher;
import org.testifj.annotations.DSL;

@DSL
public final class ExceptionWhere {

    public static <E extends Throwable> Matcher<E> messageIs(Matcher<String> matcher) {
        return e -> {
            assert e != null : "Exception can't be null";
            return matcher.matches(e.getMessage());
        };
    }

}
