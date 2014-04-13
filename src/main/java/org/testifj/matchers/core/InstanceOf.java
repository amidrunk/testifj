package org.testifj.matchers.core;

import org.testifj.Matcher;

public final class InstanceOf {

    public static <T> Matcher<T> instanceOf(Class<T> type) {
        assert type != null : "Type can't be null";

        return instance -> instance != null && type.isInstance(instance);
    }

}
