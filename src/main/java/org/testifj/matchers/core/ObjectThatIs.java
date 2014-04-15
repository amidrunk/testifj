package org.testifj.matchers.core;

import org.testifj.Matcher;

public final class ObjectThatIs {

    public static <T> Matcher<T> equalTo(T other) {
        return Equal.equal(other);
    }

    public static <T> Matcher<T> sameAs(T other) { return instance -> instance == other; }

    @SuppressWarnings("unchecked")
    public static <T> InstanceOfMatcher<T> instanceOf(Class<? extends T> type) {
        assert type != null : "Type can't be null";
        return type::isInstance;
    }

    public interface InstanceOfMatcher<T> extends Matcher<T> {
        default Matcher<T> thatIs(Matcher<T> matcher) {
            assert matcher != null : "Matcher can't be null";

            return instance -> InstanceOfMatcher.this.matches(instance) && matcher.matches(instance);
        }
    }

}
