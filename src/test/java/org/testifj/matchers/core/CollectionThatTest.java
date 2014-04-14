package org.testifj.matchers.core;

import org.junit.Test;
import org.testifj.Matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class CollectionThatTest {

    @Test
    public void containsElementShouldNotAcceptNullPredicate() {
        expect(() -> CollectionThat.containElement(null)).toThrow(AssertionError.class);
    }

    @Test
    public void containsElementMatcherShouldNotMatchEmptyCollection() {
        final Matcher<Collection<Object>> matcher = CollectionThat.containElement((e) -> true);

        expect(matcher.matches(Collections.emptyList())).toBe(false);
    }

    @Test
    public void containsElementMatcherShouldReturnFalseIfPredicateIsFalseForAllElements() {
        final Matcher<Collection<Object>> matcher = CollectionThat.containElement((e) -> false);

        expect(matcher.matches(Arrays.asList("foo"))).toBe(false);
    }

    @Test
    public void containsElementMatcherShouldReturnTrueIfPredicateIsTrueForAnyElement() {
        final Matcher<Collection<Object>> matcher = CollectionThat.containElement((e) -> e.equals("bar"));

        expect(matcher.matches(Arrays.asList("foo", "bar", "baz"))).toBe(true);
    }

}
