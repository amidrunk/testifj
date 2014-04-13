package org.testifj.matchers.core;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class ObjectThatIsTest {

    @Test
    public void equalToMatcherShouldMatchBothNull() {
        expect(ObjectThatIs.equalTo(null).matches(null)).toBe(true);
    }

    @Test
    public void equalToMatcherShouldMatchEqualInstances() {
        expect(ObjectThatIs.equalTo("foo").matches("foo")).toBe(true);
    }

    @Test
    public void equalToMatcherShouldNotMatchUnEqualInstances() {
        expect(ObjectThatIs.equalTo("foo").matches("bar")).toBe(false);
    }

    @Test
    public void equalToMatcherShouldMatchEqualArrays() {
        expect(ObjectThatIs.equalTo(new String[]{"foo", "bar"}).matches(new String[]{"foo", "bar"})).toBe(true);
    }

    @Test
    public void equalToMatcherShouldNotMatchDifferentArrays() {
        expect(ObjectThatIs.equalTo(new String[]{"foo", "bar"}).matches(new String[]{"foo", "baz"})).toBe(false);
    }

}
