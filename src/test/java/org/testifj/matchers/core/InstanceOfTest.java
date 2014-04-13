package org.testifj.matchers.core;

import org.junit.Test;
import org.testifj.Matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testifj.matchers.core.InstanceOf.instanceOf;

@SuppressWarnings("unchecked")
public class InstanceOfTest {

    @Test(expected = AssertionError.class)
    public void instanceOfShouldNotAcceptNullType() {
        instanceOf(null);
    }

    @Test
    public void matcherShouldReturnFalseForNull() {
        assertFalse(((Matcher) instanceOf(String.class)).matches(null));
    }

    @Test
    public void matcherShouldNotMatchForIncorrectType() {
        assertFalse(((Matcher) instanceOf(String.class)).matches(1234));
    }

    @Test
    public void matcherShouldMatchForCorrectType() {
        assertTrue(instanceOf(String.class).matches("foo"));
    }

    @Test
    public void matcherShouldReturnTrueForSubTypeOfRequestedType() {
        final Runnable runnable = () -> {
        };

        assertTrue(instanceOf(Runnable.class).matches(runnable));
    }

}
