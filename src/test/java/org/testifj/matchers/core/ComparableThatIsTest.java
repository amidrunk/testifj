package org.testifj.matchers.core;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ComparableThatIs.comparableTo;
import static org.testifj.matchers.core.ComparableThatIs.greaterThan;
import static org.testifj.matchers.core.ComparableThatIs.lessThan;

public class ComparableThatIsTest {

    @Test
    public void comparableToShouldNotAcceptNullComparable() {
        expect(() -> comparableTo(null)).toThrow(AssertionError.class);
    }

    @Test
    public void comparableToShouldNotMatchNull() {
        expect(comparableTo(BigDecimal.TEN).matches(null)).toBe(false);
    }

    @Test
    public void comparableToShouldNotMatchUnEqualComparable() {
        expect(comparableTo(BigDecimal.TEN).matches(BigDecimal.ONE)).toBe(false);
    }

    @Test
    public void comparableToShouldMatchEqualComparable() {
        expect(comparableTo(BigDecimal.TEN).matches(BigDecimal.TEN)).toBe(true);
    }

    @Test
    public void lessThanShouldNotMatchNull() {
        expect(lessThan(BigDecimal.TEN).matches(null)).toBe(false);
    }

    @Test
    public void lessThanShouldNotMatchGreaterValue() {
        expect(lessThan(BigDecimal.ONE).matches(BigDecimal.TEN)).toBe(false);
    }

    @Test
    public void lessThanShouldMatchSmallerValue() {
        expect(lessThan(BigDecimal.TEN).matches(BigDecimal.ONE)).toBe(true);
    }

    @Test
    public void lessThanShouldNotAcceptNullComparableValue() {
        expect(() -> lessThan(null)).toThrow(AssertionError.class);
    }

    @Test
    public void greaterThanShouldNotAcceptNullComparableValue() {
        expect(() -> greaterThan(null)).toThrow(AssertionError.class);
    }

    @Test
    public void greaterThanShouldNotMatchNull() {
        expect(greaterThan(BigDecimal.TEN).matches(null)).toBe(false);
    }

    @Test
    public void greaterThanShouldNotMatchSmallerValue() {
        expect(greaterThan(BigDecimal.TEN).matches(BigDecimal.ONE)).toBe(false);
    }

    @Test
    public void greaterThanShouldMatchGreaterValue() {
        expect(greaterThan(BigDecimal.ONE).matches(BigDecimal.TEN)).toBe(true);
    }

}