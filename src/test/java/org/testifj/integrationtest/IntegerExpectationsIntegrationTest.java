package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.matchers.core.IntegerThatIs;

import static org.testifj.Expectations.expect;

public class IntegerExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void integerCanBeExpectedToBeEqualToNumber() {
        expectNoException(() -> expect(1234).toEqual(1234));
        expectAssertionError(() -> expect(1234).toEqual(1235), "expected [1234] to equal 1235");
    }

    @Test
    public void integerCanBeExpectedToBeLessThanNumber() {
        expectNoException(() -> expect(9).toBeLessThan(10));
        expectNoException(() -> expect(9).toBe(IntegerThatIs.lessThan(10)));
        expectAssertionError(() -> expect(10).toBeLessThan(9), "expected [10] to be less than 9");
        expectAssertionError(() -> expect(10).toBe(IntegerThatIs.lessThan(9)), "expected [10] to be less than 9");
    }

    @Test
    public void integerCanBeExpectedToBeGreaterThanNumber() {
        expectNoException(() -> expect(10).toBeGreaterThan(9));
        expectNoException(() -> expect(10).toBe(IntegerThatIs.greaterThan(9)));
        expectAssertionError(() -> expect(9).toBeGreaterThan(10), "expected [9] to be greater than 10");
        expectAssertionError(() -> expect(9).toBe(IntegerThatIs.greaterThan(10)), "expected [9] to be greater than 10");
    }
}
