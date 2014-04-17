package org.testifj.matchers.core;

import org.testifj.Matcher;

import java.util.Optional;

public final class OptionalThatIs {

    public static <T> Matcher<Optional<T>> present() {
        return Optional<T>::isPresent;
    }

}
