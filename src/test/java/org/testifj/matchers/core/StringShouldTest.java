package org.testifj.matchers.core;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringShouldTest {

    @Test(expected = AssertionError.class)
    public void containStringShouldNotAcceptNullArgument() {
        StringShould.containString(null);
    }

    @Test
    public void containStringMatcherShouldNotMatchStringWithoutSubstring() {
        assertFalse(StringShould.containString("foo").matches("bar"));
    }

    @Test
    public void containStringShouldMatchStringWithSubstring() {
        assertTrue(StringShould.containString("foo").matches("foobar"));
    }

}
