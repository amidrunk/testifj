package org.testifj.framework;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ValidVerificationResultTest {

    @Test
    public void singleValueVerificationResultCanBeCreated() {
        final Criterion criterion = mock(Criterion.class);
        final ValidVerificationResult result = ValidVerificationResult.single(criterion);

        assertSame(criterion, result.getCriterion());
        assertEquals(VerificationOutcome.VALID, result.getOutcome());
        assertTrue(result.getSubVerificationResults().isEmpty());
    }

    @Test
    public void compositeVerificationResultCanBeCreated() {
        final Criterion criterion = mock(Criterion.class);
        final VerificationResult subResult1 = mock(VerificationResult.class);
        final VerificationResult subResult2 = mock(VerificationResult.class);
        final ValidVerificationResult result = ValidVerificationResult.composite(criterion, Arrays.asList(subResult1, subResult2));

        assertSame(criterion, result.getCriterion());
        assertEquals(VerificationOutcome.VALID, result.getOutcome());
        assertEquals(Arrays.asList(subResult1, subResult2), result.getSubVerificationResults());
    }
}