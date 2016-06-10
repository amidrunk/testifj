package org.testifj.framework;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class UnsupportedVerificationResultTest {

    private final Criterion criterion = mock(Criterion.class);

    private final UnsupportedVerificationResult result = new UnsupportedVerificationResult(criterion);

    @Test
    public void resultShouldContainCriterion() {
        assertSame(criterion, result.getCriterion());
    }

    @Test
    public void subVerificationResultsShouldAlwaysBeEmpty() {
        assertTrue(result.getSubVerificationResults().isEmpty());
    }

    @Test
    public void outcomeShouldBeNotSupported() {
        assertEquals(VerificationOutcome.NOT_SUPPORTED, result.getOutcome());
    }
}