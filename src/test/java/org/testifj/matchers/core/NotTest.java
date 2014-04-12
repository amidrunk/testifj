package org.testifj.matchers.core;

import org.junit.Test;
import org.testifj.Matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotTest {

    @Test
    @SuppressWarnings("unchecked")
    public void notMatcherShouldInvertTargetMatcher() {
        final Matcher target = mock(Matcher.class);

        when(target.matches(eq("foo"))).thenReturn(true);
        when(target.matches(eq("bar"))).thenReturn(false);

        final Matcher notMatcher = Not.not(target);

        assertFalse(notMatcher.matches("foo"));
        assertTrue(notMatcher.matches("bar"));
    }

}
