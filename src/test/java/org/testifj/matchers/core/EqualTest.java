package org.testifj.matchers.core;

import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

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
    public void failedExpectationShouldContainDescription() {
        final Supplier<String> supplier = () -> "foo";

        boolean failed = false;

        try {
            expect(supplier.get()).toBe("bar");
        } catch (AssertionError e) {
            e.printStackTrace();
            expect(e.getMessage()).toBe("Expected supplier.get() => \"foo\" to be \"bar\"");
            failed = true;
        }

        expect(failed).toBe(true);
    }

}
