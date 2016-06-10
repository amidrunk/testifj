package org.testifj.matchers.core;

import org.testifj.Matcher;
import io.recode.annotations.DSL;

import java.util.regex.Pattern;

@DSL
public final class StringThatIs {

    public static Matcher<String> stringContaining(String subString) {
        assert subString != null && !subString.isEmpty() : "Parameter 'subString' can't be null or empty";

        return string -> string != null && string.contains(subString);
    }

    public static Matcher<String> empty() {
        return string -> string != null && string.isEmpty();
    }

    public static Matcher<String> notEmpty() {
        return string -> string != null && !string.isEmpty();
    }

    public static Matcher<String> atLeastOfLength(int minimumLength) {
        return string -> string != null && string.length() >= minimumLength;
    }

    public static Matcher<String> atMostOfLength(int maximumLength) {
        return string -> string != null && string.length() <= maximumLength;
    }

    public static Matcher<String> ofLengthLessThan(int maximumLength) {
        return string -> string != null && string.length() < maximumLength;
    }

    public static Matcher<String> ofLengthGreaterThan(int minimumLength) {
        return string -> string != null && string.length() > minimumLength;
    }

    public static Matcher<String> ofPattern(String pattern) {
        final Pattern regex = Pattern.compile(pattern);

        return string -> string != null && regex.matcher(string).matches();
    }

    public static Matcher<String> upperCase() {
        return string -> {
            if (string == null || string.isEmpty()) {
                return false;
            }

            for (int i = 0; i < string.length(); i++) {
                if (Character.isLowerCase(string.charAt(i))) {
                    return false;
                }
            }

            return true;
        };
    }

    public static Matcher<String> lowerCase() {
        return string -> {
            if (string == null || string.isEmpty()) {
                return false;
            }

            for (int i = 0; i < string.length(); i++) {
                if (Character.isUpperCase(string.charAt(i))) {
                    return false;
                }
            }

            return true;
        };
    }

    public static Matcher<String> alphaNumeric() {
        return string -> {
            if (string == null || string.isEmpty()) {
                return false;
            }

            for (int i = 0; i < string.length(); i++) {
                if (!Character.isAlphabetic(string.charAt(i)) && !Character.isDigit(string.charAt(i))) {
                    return false;
                }
            }

            return true;
        };
    }


}
