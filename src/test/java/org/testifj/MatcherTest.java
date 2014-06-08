package org.testifj;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;

@SuppressWarnings("unchecked")
public class MatcherTest {

    @Test
    public void andShouldNotAcceptInvalidArgument() {
        final Matcher matcher = (e) -> true;

        expect(() -> matcher.and(null)).toThrow(AssertionError.class);
    }

    @Test
    public void andShouldFailIfBothMatchersDoNotMatch() {
        final Matcher matcher1 = (e) -> false;
        final Matcher matcher2 = (e) -> false;
        final Matcher matcher3 = (e) -> true;

        expect(matcher1.and(matcher2).matches("foo")).toBe(false);
        expect(matcher1.and(matcher3).matches("foo")).toBe(false);
        expect(matcher3.and(matcher1).matches("foo")).toBe(false);
    }

    @Test
    public void andShouldMatchIfBothMatchersMatch() {
        final Matcher matcher1 = (e) -> true;
        final Matcher matcher2 = (e) -> true;

        expect(matcher1.and(matcher2).matches("foo")).toBe(true);
    }

    @Test
    public void orShouldNotAcceptInvalidArgument() {
        final Matcher matcher = (e) -> true;

        expect(() -> matcher.or(null)).toThrow(AssertionError.class);
    }

    @Test
    public void orShouldNotMatchIfNoMatcherMatches() {
        final Matcher matcher1 = (e) -> false;
        final Matcher matcher2 = (e) -> false;

        expect(matcher1.or(matcher2).matches("foo")).toBe(false);
        expect(matcher2.or(matcher1).matches("foo")).toBe(false);
    }

    @Test
    public void orShouldMatchIfAnyMatcherMatches() {
        final Matcher matcher1 = (e) -> true;
        final Matcher matcher2 = (e) -> true;
        final Matcher matcher3 = (e) -> false;

        expect(matcher1.or(matcher2).matches("foo")).toBe(true);
        expect(matcher2.or(matcher1).matches("foo")).toBe(true);
        expect(matcher1.or(matcher3).matches("foo")).toBe(true);
        expect(matcher3.or(matcher1).matches("foo")).toBe(true);
    }

}