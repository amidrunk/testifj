package org.testifj.matchers.core;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EqualTest {

    @Test
    public void equalMatcherShouldReturnTrueForNullAndNull() {
        assertTrue(Equal.equal(null).matches(null));
    }

    @Test
    public void equalMatcherShouldReturnFalseForNullAndNonNull() {
        assertFalse(Equal.equal(null).matches("foo"));
    }

    @Test
    public void equalMatcherShouldReturnFalseForNonNullAndNull() {
        assertFalse(Equal.equal("foo").matches(null));
    }

    @Test
    public void equalMatcherShouldNotMatchForUnEqualInstances() {
        assertFalse(Equal.equal("foo").matches("bar"));
    }

    @Test
    public void equalMatcherShouldMatchForEqualInstances() {
        assertTrue(Equal.equal("foo").matches("foo"));
    }

    @Test
    public void equalMatcherShouldMatchForEqualArrays() {
        assertTrue(Equal.equal(new byte[] {1, 2, 3, 4}).matches(new byte[]{1, 2, 3, 4}));
    }

}
