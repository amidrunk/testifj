package org.testifj.matchers.core;

import org.testifj.Matcher;

public final class ObjectThatIs {

    public static <T> Matcher<T> equalTo(T other) {
        return Equal.equal(other);
    }

}
