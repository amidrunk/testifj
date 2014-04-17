package org.testifj.matchers.core;

import org.testifj.Matcher;
import org.testifj.annotations.DSL;

import java.util.Optional;

@DSL
public final class OptionalThatIs {

    public static <T> Matcher<Optional<T>> present() {
        return Optional<T>::isPresent;
    }

}
