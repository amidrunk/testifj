package org.testifj;

/**
 * TODO Should not return true false, but rather a match-result? No... would be cool though, since it would
 * be possible to determine which precise matcher failed in a sequence.
 */
@FunctionalInterface
public interface Matcher<T> {

    boolean matches(T instance);

    default Matcher<T> or(Matcher<T> matcher)  {
        assert matcher != null : "Matcher can't be null";

        return (e) -> Matcher.this.matches(e) || matcher.matches(e);
    }

    default Matcher<T> and(Matcher<T> matcher) {
        assert matcher != null : "Matcher can't be null";

        return (e) -> Matcher.this.matches(e) && matcher.matches(e);
    }
}
