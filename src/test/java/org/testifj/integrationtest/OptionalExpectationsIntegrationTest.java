package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.Expectations;

import java.util.Optional;

import static org.testifj.Expectations.expect;

public class OptionalExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void optionalCanBeExpectedToBeEmpty() {
        expectNoException(() -> expect(Optional.empty()).toBeEmpty());
        expectAssertionError(() -> expect(Optional.of("foo")).toBeEmpty(), "expected [Optional.of(\"foo\")] => Optional[foo] to be empty");
    }

    @Test
    public void optionalCanBeExpectedToContainObject() {
        expectNoException(() -> expect(Optional.of("foo")).toContain("foo"));
        expectAssertionError(() -> expect(Optional.of("foo")).toContain("bar"), "expected [Optional.of(\"foo\")] => Optional[foo] to contain [\"bar\"]");
    }
}
