package org.testifj.matchers.core;

import org.testifj.Matcher;

public class Equal {

    public static<T> Matcher<T> equal(T expectedInstance) {
        return otherInstance -> otherInstance == expectedInstance || otherInstance != null && otherInstance.equals(expectedInstance);
    }

}
