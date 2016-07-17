package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.matchers.core.ArrayThatIs;

import static org.testifj.Expectations.expect;

public class ArrayExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void arrayCanBeExpectedToEqualAnotherArray() {
        expectNoException(() -> expect(new String[] {"foo", "bar"}).toEqual(new String[] {"foo", "bar"}));
        expectAssertionError(() -> expect(new String[] {"foo", "bar"}).toEqual(new String[] {"foo", "baz"}), "expected [new String[] { \"foo\", \"bar\" }] => [\"foo\", \"bar\"] to equal new String[] { \"foo\", \"baz\" }");
    }

    @Test
    public void arrayCanBeExpectedToHaveASpecificLength() {
        final String[] array = { "foo", "bar" };

        expectNoException(() -> expect(array).toHaveLength(2));
        expectNoException(() -> expect(array).toBe(ArrayThatIs.ofLength(2)));
        expectAssertionError(() -> expect(array).toHaveLength(3), "expected [array] => [\"foo\", \"bar\"] to have length 3");
        expectAssertionError(() -> expect(array).toBe(ArrayThatIs.ofLength(3)), "expected [array] => [\"foo\", \"bar\"] to be of length 3");
    }
}
