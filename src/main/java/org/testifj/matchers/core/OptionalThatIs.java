package org.testifj.matchers.core;

import org.testifj.Matcher;
import org.testifj.annotations.DSL;

import java.util.Optional;

@DSL
public final class OptionalThatIs {

    public static <T> Matcher<Optional<T>> present() {
        return Optional<T>::isPresent;
    }

    public static <T> Matcher<Optional<T>> optionalOf(T instance) {
        assert instance != null : "Instance can't be null";

        return e -> {
            if (e == null) {
                return false;
            }

            if (!e.isPresent()) {
                return false;
            }

            return instance.equals(e.get());
        };
    }

}
