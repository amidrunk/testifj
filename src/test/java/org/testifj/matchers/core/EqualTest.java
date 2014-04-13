package org.testifj.matchers.core;

import org.junit.Ignore;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;
import static org.testifj.matchers.core.StringShould.containString;

public class EqualTest {

    @Test
    public void equalMatcherShouldReturnTrueForNullAndNull() {
        assertTrue(equal(null).matches(null));
    }

    @Test
    public void equalMatcherShouldReturnFalseForNullAndNonNull() {
        assertFalse(equal(null).matches("foo"));
    }

    @Test
    public void equalMatcherShouldReturnFalseForNonNullAndNull() {
        assertFalse(equal("foo").matches(null));
    }

    @Test
    public void equalMatcherShouldNotMatchForUnEqualInstances() {
        assertFalse(equal("foo").matches("bar"));
    }

    @Test
    public void equalMatcherShouldMatchForEqualInstances() {
        assertTrue(equal("foo").matches("foo"));
    }

    @Test
    public void equalMatcherShouldMatchForEqualArrays() {
        assertTrue(equal(new byte[]{1, 2, 3, 4}).matches(new byte[]{1, 2, 3, 4}));
    }

    @Test
    @Ignore("Add when Expect generates descriptions")
    public void failedExpectationShouldContainDescription() {
        final Supplier<String> supplier = () -> "foo";

        boolean failed = false;

        try {
            expect(supplier.get()).to(equal("bar"));
        } catch (AssertionError e) {
            failed = true;

            expect(e.getMessage()).to(containString("supplier.get()"));
            expect(e.getMessage()).to(containString("\"foo\""));
            expect(e.getMessage()).to(containString("\"bar\""));
        }

        expect(failed).toBe(true);
    }

}
