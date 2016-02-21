package org.testifj.matchers.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerThatIsTest {

    @Test
    public void negativeShouldOnlyMatchNegativeNumbers() {
        assertTrue(IntegerThatIs.negative().matches(-100));
        assertTrue(IntegerThatIs.negative().matches(-1));
        assertFalse(IntegerThatIs.negative().matches(0));
        assertFalse(IntegerThatIs.negative().matches(1));
        assertFalse(IntegerThatIs.negative().matches(100));
        assertFalse(IntegerThatIs.negative().matches(null));
    }

    @Test
    public void positiveShouldOnlyMatchZeroOrNatural() {
        assertTrue(IntegerThatIs.positive().matches(0));
        assertTrue(IntegerThatIs.positive().matches(1));
        assertTrue(IntegerThatIs.positive().matches(100));
        assertFalse(IntegerThatIs.positive().matches(-1));
        assertFalse(IntegerThatIs.positive().matches(-100));
        assertFalse(IntegerThatIs.positive().matches(null));
    }

    @Test
    public void naturalShouldOnlyMatchIntegerGreaterThanZero() {
        assertTrue(IntegerThatIs.natural().matches(1));
        assertTrue(IntegerThatIs.natural().matches(100));
        assertFalse(IntegerThatIs.natural().matches(0));
        assertFalse(IntegerThatIs.natural().matches(-1));
        assertFalse(IntegerThatIs.natural().matches(-100));
        assertFalse(IntegerThatIs.natural().matches(null));
    }

    @Test
    public void evenShouldMatchOnlyEvenPositiveOrNegativeNumbers() {
        assertTrue(IntegerThatIs.even().matches(-4));
        assertTrue(IntegerThatIs.even().matches(-2));
        assertTrue(IntegerThatIs.even().matches(0));
        assertTrue(IntegerThatIs.even().matches(2));
        assertTrue(IntegerThatIs.even().matches(4));
        assertFalse(IntegerThatIs.even().matches(-3));
        assertFalse(IntegerThatIs.even().matches(-1));
        assertFalse(IntegerThatIs.even().matches(1));
        assertFalse(IntegerThatIs.even().matches(3));
        assertFalse(IntegerThatIs.even().matches(null));
    }

    @Test
    public void oddShouldMatchOnlyOddPositiveOrNegativeNumbers() {
        assertTrue(IntegerThatIs.odd().matches(-3));
        assertTrue(IntegerThatIs.odd().matches(-1));
        assertTrue(IntegerThatIs.odd().matches(1));
        assertTrue(IntegerThatIs.odd().matches(3));
        assertFalse(IntegerThatIs.odd().matches(-2));
        assertFalse(IntegerThatIs.odd().matches(0));
        assertFalse(IntegerThatIs.odd().matches(2));
        assertFalse(IntegerThatIs.odd().matches(null));
    }

    @Test
    public void greaterThanShouldOnlyMatchGreaterIntegers() {
        assertTrue(IntegerThatIs.greaterThan(10).matches(11));
        assertTrue(IntegerThatIs.greaterThan(10).matches(110));
        assertFalse(IntegerThatIs.greaterThan(10).matches(10));
        assertFalse(IntegerThatIs.greaterThan(10).matches(9));
        assertFalse(IntegerThatIs.greaterThan(10).matches(0));
        assertFalse(IntegerThatIs.greaterThan(10).matches(-1));
        assertFalse(IntegerThatIs.greaterThan(10).matches(-100));
        assertFalse(IntegerThatIs.greaterThan(10).matches(null));
    }

    @Test
    public void atLeastShouldMatchGreaterOrEqualIntegers() {
        assertTrue(IntegerThatIs.atLeast(10).matches(10));
        assertTrue(IntegerThatIs.atLeast(10).matches(11));
        assertTrue(IntegerThatIs.atLeast(10).matches(110));
        assertFalse(IntegerThatIs.atLeast(10).matches(9));
        assertFalse(IntegerThatIs.atLeast(10).matches(1));
        assertFalse(IntegerThatIs.atLeast(10).matches(0));
        assertFalse(IntegerThatIs.atLeast(10).matches(-1));
        assertFalse(IntegerThatIs.atLeast(10).matches(-10));
        assertFalse(IntegerThatIs.atLeast(10).matches(null));
    }

    @Test
    public void lessThanShouldMatchSmallerIntegers() {
        assertTrue(IntegerThatIs.lessThan(10).matches(9));
        assertTrue(IntegerThatIs.lessThan(10).matches(1));
        assertTrue(IntegerThatIs.lessThan(10).matches(0));
        assertTrue(IntegerThatIs.lessThan(10).matches(-1));
        assertTrue(IntegerThatIs.lessThan(10).matches(-100));
        assertFalse(IntegerThatIs.lessThan(10).matches(10));
        assertFalse(IntegerThatIs.lessThan(10).matches(11));
        assertFalse(IntegerThatIs.lessThan(10).matches(110));
        assertFalse(IntegerThatIs.lessThan(10).matches(null));
    }

    @Test
    public void atMostShouldMatchSmallerOrEqualIntegers() {
        assertTrue(IntegerThatIs.atMost(10).matches(10));
        assertTrue(IntegerThatIs.atMost(10).matches(9));
        assertTrue(IntegerThatIs.atMost(10).matches(1));
        assertTrue(IntegerThatIs.atMost(10).matches(0));
        assertTrue(IntegerThatIs.atMost(10).matches(-1));
        assertTrue(IntegerThatIs.atMost(10).matches(-10));
        assertTrue(IntegerThatIs.atMost(10).matches(-100));
        assertFalse(IntegerThatIs.atMost(10).matches(11));
        assertFalse(IntegerThatIs.atMost(10).matches(110));
        assertFalse(IntegerThatIs.atMost(10).matches(null));
    }

    @Test
    public void betweenShouldMatchIntegersInRangeInclusive() {
        assertTrue(IntegerThatIs.between(5, 10).matches(5));
        assertTrue(IntegerThatIs.between(5, 10).matches(6));
        assertTrue(IntegerThatIs.between(5, 10).matches(7));
        assertTrue(IntegerThatIs.between(5, 10).matches(8));
        assertTrue(IntegerThatIs.between(5, 10).matches(9));
        assertTrue(IntegerThatIs.between(5, 10).matches(10));
        assertFalse(IntegerThatIs.between(5, 10).matches(4));
        assertFalse(IntegerThatIs.between(5, 10).matches(11));
        assertFalse(IntegerThatIs.between(5, 10).matches(0));
        assertFalse(IntegerThatIs.between(5, 10).matches(-1));
        assertFalse(IntegerThatIs.between(5, 10).matches(null));
    }
}