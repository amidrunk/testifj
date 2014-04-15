package org.testifj.matchers.core;

import org.junit.Test;
import org.testifj.Matcher;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ObjectThatIsTest {

    @Test
    public void equalToMatcherShouldMatchBothNull() {
        expect(equalTo(null).matches(null)).toBe(true);
    }

    @Test
    public void equalToMatcherShouldMatchEqualInstances() {
        expect(equalTo("foo").matches("foo")).toBe(true);
    }

    @Test
    public void equalToMatcherShouldNotMatchUnEqualInstances() {
        expect(equalTo("foo").matches("bar")).toBe(false);
    }

    @Test
    public void equalToMatcherShouldMatchEqualArrays() {
        expect(equalTo(new String[]{"foo", "bar"}).matches(new String[]{"foo", "bar"})).toBe(true);
    }

    @Test
    public void equalToMatcherShouldNotMatchDifferentArrays() {
        expect(equalTo(new String[]{"foo", "bar"}).matches(new String[]{"foo", "baz"})).toBe(false);
    }

    @Test
    public void sameAsShouldMatchSameInstance() {
        final String str = "foo";

        expect(ObjectThatIs.sameAs(str).matches(str)).toBe(true);
    }

    @Test
    public void sameAsShouldNotMatchNonEqualInstance() {
        expect(ObjectThatIs.sameAs("foo").matches("bar")).toBe(false);
    }

    @Test
    public void sameAsShouldNotMatchEqualInstanceIfNotSameInstance() {
        expect(ObjectThatIs.sameAs("foo").matches(new String("foo"))).toBe(false);
    }

    @Test
    public void instanceOfShouldNotAcceptNullArg() {
        expect(() -> ObjectThatIs.instanceOf(null)).toThrow(AssertionError.class);
    }

    @Test
    public void instanceOfShouldMatchInstanceOfSameType() {
        expect(ObjectThatIs.instanceOf(String.class).matches("foo")).toBe(true);
    }

    @Test
    public void instanceOfShouldMatchInstanceOfDerivedType() {
        expect(ObjectThatIs.instanceOf(CharSequence.class).matches("foo")).toBe(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void instanceShouldNotMatchInstanceOfIncompatibleType() {
        Class cls = CharSequence.class;

        expect(ObjectThatIs.instanceOf(cls).matches(1234)).toBe(false);
    }

    @Test
    public void instanceOfMatchShouldNotMatchIfAdditionalMatchersFail() {
        expect(ObjectThatIs.instanceOf(String.class).thatIs(equalTo("bar")).matches("foo")).toBe(false);
    }

    @Test
    public void instanceOfMatchShouldMatchIfTypeMatchesAndAdditionalMatchersMatches() {
        expect(ObjectThatIs.instanceOf(String.class).thatIs(equalTo("foo")).matches("foo")).toBe(true);
    }

    @Test
    public void additionalInstanceOfMatchersCannotBeNull() {
        expect(() -> ObjectThatIs.instanceOf(String.class).thatIs(null)).toThrow(AssertionError.class);
    }

}
