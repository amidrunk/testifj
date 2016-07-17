package org.testifj.matchers.core;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BooleanThatIsTest {

    @Test
    public void trueMatcherShouldMatchTrueOnly() {
        assertTrue(BooleanThatIs.equalToTrue().matches(true));
        assertFalse(BooleanThatIs.equalToTrue().matches(false));
        assertFalse(BooleanThatIs.equalToTrue().matches(null));
    }

    @Test
    public void falseMatcherShouldMatchFalseOnly() {
        assertTrue(BooleanThatIs.equalToFalse().matches(false));
        assertFalse(BooleanThatIs.equalToFalse().matches(true));
        assertFalse(BooleanThatIs.equalToFalse().matches(null));
    }
}