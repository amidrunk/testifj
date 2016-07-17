package org.testifj.integrationtest;

import org.junit.Test;

import static org.testifj.Expectations.expect;

public class BooleanExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void booleanCanBeExpectedToBeTrue() {
        expectNoException(() -> expect(true).toBeTrue());
        expectAssertionError(() -> expect(false).toBeTrue(), "expected [false] to be true");
    }

    @Test
    public void booleanCanBeExpectedToBeFalse() {
        expectNoException(() -> expect(false).toBeFalse());
        expectAssertionError(() -> expect(true).toBeFalse(), "expected [true] to be false");
    }
}
