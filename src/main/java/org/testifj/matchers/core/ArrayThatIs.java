package org.testifj.matchers.core;

import io.recode.annotations.DSL;
import org.testifj.Matcher;

import java.util.Objects;

@DSL
public final class ArrayThatIs {

    public static <T> Matcher<T[]> ofLength(int length) {
        assert length >= 0 : "Length must be positive";

        return instance -> instance.length == length;
    }

    public static Matcher<int[]> arrayWith(int ... elements) {
        assert elements != null : "Elements can't be null";

        return array -> {
            if (elements.length > array.length) {
                return false;
            }

            for (int n1 : elements) {
                boolean matchFound = false;

                for (int n2 : array) {
                    if (n1 == n2) {
                        matchFound = true;
                        break;
                    }
                }

                if (!matchFound) {
                    return false;
                }
            }

            return true;
        };
    }

    public static Matcher<int[]> arrayOf(int ... elements) {
        assert elements != null : "Elements can't be null";

        return intArray -> {
            if (intArray.length != elements.length) {
                return false;
            }

            for (int i = 0; i < elements.length; i++) {
                if (elements[i] != intArray[i]) {
                    return false;
                }
            }

            return true;
        };
    }

    public static <T> Matcher<T[]> arrayOf(T ... elements) {
        assert elements != null : "Elements can't be null";

        return array -> {
            if (array.length != elements.length) {
                return false;
            }

            for (int i = 0; i < elements.length; i++) {
                if (!Objects.equals(elements[i], array[i])) {
                    return false;
                }
            }

            return true;
        };
    }

}
