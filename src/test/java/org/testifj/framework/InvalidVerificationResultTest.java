package org.testifj.framework;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class InvalidVerificationResultTest {

    @Test
    public void singleFailedVerificationResultCanBeCreated() {
        final Criterion criterion = mock(Criterion.class);
        final InvalidVerificationResult result = InvalidVerificationResult.single(criterion);

        assertSame(criterion, result.getCriterion());
        assertEquals(VerificationOutcome.INVALID, result.getOutcome());
        assertTrue(result.getSubVerificationResults().isEmpty());
    }

    @Test
    public void compositeFailedVerificationResultCanBeCreated() {
        final Criterion criterion = mock(Criterion.class);
        final VerificationResult subVerificationResult1 = mock(VerificationResult.class);
        final VerificationResult subVerificationResult2 = mock(VerificationResult.class);
        final InvalidVerificationResult result = InvalidVerificationResult.composite(criterion, Arrays.asList(subVerificationResult1, subVerificationResult2));

        assertSame(criterion, result.getCriterion());
        assertEquals(VerificationOutcome.INVALID, result.getOutcome());
        assertEquals(Arrays.asList(subVerificationResult1, subVerificationResult2), result.getSubVerificationResults());
    }
}