package org.testifj.matchers.core;

import org.testifj.Matcher;

public final class StringShould {

    public static Matcher<String> containString(String subString) {
        assert subString != null : "Sub-string can't be null";
        return string -> string.contains(subString);
    }

}
