package org.testifj.matchers.core;

import org.testifj.Matcher;

import java.lang.reflect.Array;

public class Equal {

    public static <T> Matcher<T> equal(T expectedInstance) {
        return otherInstance -> {
            if (otherInstance == expectedInstance) {
                return true;
            }

            if (expectedInstance == null || otherInstance == null) {
                return false;
            }

            if (expectedInstance.equals(otherInstance)) {
                return true;
            }

            if (expectedInstance.getClass().isArray()) {
                final int expectedLength = Array.getLength(expectedInstance);
                final int actualLength = Array.getLength(otherInstance);

                if (expectedLength != actualLength) {
                    return false;
                }

                for (int i = 0; i < expectedLength; i++) {
                    if (!equal(Array.get(expectedInstance, i)).matches(Array.get(otherInstance, i))) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        };
    }

}
