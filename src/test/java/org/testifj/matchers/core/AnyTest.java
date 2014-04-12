package org.testifj.matchers.core;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.testifj.matchers.core.Any.any;

public class AnyTest {

    @Test
    public void anyMatcherShouldMatchAnything() {
        assertTrue(any().matches("foo"));
    }

}
