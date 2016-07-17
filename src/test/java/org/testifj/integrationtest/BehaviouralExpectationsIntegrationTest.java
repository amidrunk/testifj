package org.testifj.integrationtest;

import org.junit.Test;

import static org.testifj.Expectations.expect;

public class BehaviouralExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void procedureCanBeExpectedToThrowExceptionOfType() {
        expectNoException(() -> expect(() -> { throw new IllegalArgumentException(); }).toThrow(IllegalArgumentException.class));
        expectAssertionError(() -> expect(() -> {}).toThrow(IllegalArgumentException.class), "expected [{}] to throw IllegalArgumentException");
    }

    @Test
    public void procedureWithCodeCanBeExpectedToThrowExceptionType() {
        expectAssertionError(() -> expect(() -> Integer.parseInt("0")).toThrow(IllegalArgumentException.class), "expected [Integer.parseInt(\"0\")] to throw IllegalArgumentException");
    }
}
