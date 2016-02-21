package org.testifj.matchers.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class NumberThatIsTest {

    @Test
    public void greaterThanShouldOnlyMatcherGreaterNumbers() {
        assertTrue(NumberThatIs.greaterThan(5).matches(6));
        assertTrue(NumberThatIs.greaterThan(5).matches(7));
        assertFalse(NumberThatIs.greaterThan(5).matches(-1));
        assertFalse(NumberThatIs.greaterThan(5).matches(0));
        assertFalse(NumberThatIs.greaterThan(5).matches(1));
        assertFalse(NumberThatIs.greaterThan(5).matches(5));
        assertFalse(NumberThatIs.greaterThan(5).matches(null));
    }

    @Test
    public void atLeastShouldOnlyMatchGreaterOrEqualNumbers() {
        assertTrue(NumberThatIs.atLeast(5).matches(5));
        assertTrue(NumberThatIs.atLeast(5).matches(6));
        assertFalse(NumberThatIs.atLeast(5).matches(-1));
        assertFalse(NumberThatIs.atLeast(5).matches(0));
        assertFalse(NumberThatIs.atLeast(5).matches(1));
        assertFalse(NumberThatIs.atLeast(5).matches(4));
        assertFalse(NumberThatIs.atLeast(5).matches(null));
    }

    @Test
    public void lessThanShouldOnlyMatchSmallerNumbers() {
        assertTrue(NumberThatIs.lessThan(5).matches(4));
        assertTrue(NumberThatIs.lessThan(5).matches(3));
        assertTrue(NumberThatIs.lessThan(5).matches(0));
        assertTrue(NumberThatIs.lessThan(5).matches(-1));
        assertFalse(NumberThatIs.lessThan(5).matches(5));
        assertFalse(NumberThatIs.lessThan(5).matches(6));
        assertFalse(NumberThatIs.lessThan(5).matches(null));
    }

    @Test
    public void atMostShouldOnlyMatchSmallerOrEqualNumbers() {
        assertTrue(NumberThatIs.atMost(5).matches(5));
        assertTrue(NumberThatIs.atMost(5).matches(4));
        assertTrue(NumberThatIs.atMost(5).matches(0));
        assertTrue(NumberThatIs.atMost(5).matches(-1));
        assertFalse(NumberThatIs.atMost(5).matches(6));
        assertFalse(NumberThatIs.atMost(5).matches(null));
    }

    @Test
    public void betweenShouldMatchNumberInRangeInclusive() {
        assertTrue(NumberThatIs.between(5, 10).matches(5));
        assertTrue(NumberThatIs.between(5, 10).matches(6));
        assertTrue(NumberThatIs.between(5, 10).matches(7));
        assertTrue(NumberThatIs.between(5, 10).matches(8));
        assertTrue(NumberThatIs.between(5, 10).matches(9));
        assertTrue(NumberThatIs.between(5, 10).matches(10));
        assertFalse(NumberThatIs.between(5, 10).matches(4));
        assertFalse(NumberThatIs.between(5, 10).matches(0));
        assertFalse(NumberThatIs.between(5, 10).matches(-1));
        assertFalse(NumberThatIs.between(5, 10).matches(11));
        assertFalse(NumberThatIs.between(5, 10).matches(null));
    }

}