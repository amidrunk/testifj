package org.testifj.matchers.core;

import org.testifj.Matcher;
import org.testifj.annotations.DSL;

@DSL
public final class StringThatIs {

    public static Matcher<String> stringContaining(String subString) {
        assert subString != null && !subString.isEmpty() : "Parameter 'subString' can't be null or empty";

        return string -> string != null && string.contains(subString);
    }

}
