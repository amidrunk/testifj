package org.testifj.framework;

import org.junit.Test;
import org.testifj.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchCriterionTest {

    private final Matcher matcher = mock(Matcher.class);

    private final MatchCriterion criterion = new MatchCriterion(matcher);

    @Test
    public void criterionShouldReturnNotSupportedIfExpectationIsNotAValueExpectation() {
        final VerificationResult result = criterion.verify(mock(Expectation.class));

        assertEquals(VerificationOutcome.NOT_SUPPORTED, result.getOutcome());
    }

    @Test
    public void criterionShouldBeVerifiedIfMatcherVerifies() {
        final ValueExpectation expectation = mock(ValueExpectation.class);

        when(expectation.getSubject()).thenReturn("foo");
        when(matcher.matches(eq("foo"))).thenReturn(true);

        final VerificationResult result = criterion.verify(expectation);

        assertEquals(VerificationOutcome.VALID, result.getOutcome());
        assertSame(criterion, result.getCriterion());
        assertTrue(result.getSubVerificationResults().isEmpty());
    }

    @Test
    public void criterionShouldNotBeVerifiedIfMatcherDoesNotMatch() {
        final ValueExpectation expectation = mock(ValueExpectation.class);

        when(expectation.getSubject()).thenReturn("foo");
        when(matcher.matches(eq("foo"))).thenReturn(false);

        final VerificationResult result = criterion.verify(expectation);

        assertEquals(VerificationOutcome.INVALID, result.getOutcome());
        assertSame(criterion, result.getCriterion());
        assertTrue(result.getSubVerificationResults().isEmpty());
    }
}