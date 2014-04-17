package org.testifj;

import org.testifj.matchers.core.Equal;

public interface ExpectValueContinuation<T> {

    void to(Matcher<T> matcher);

    default void toBe(Matcher<T> matcher) {
        to(matcher);
    }

    default void toBe(T instance) {
        toBe(Equal.equal(instance));
    }

    default ExpectValueContinuation<T> not() {
        return matcher -> ExpectValueContinuation.this.to(instance -> !matcher.matches(instance));
    }

}
