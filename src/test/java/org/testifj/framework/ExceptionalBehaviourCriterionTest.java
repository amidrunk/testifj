package org.testifj.framework;

import org.junit.Test;
import org.testifj.Matcher;
import org.testifj.Procedure;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExceptionalBehaviourCriterionTest {

    private final Matcher matcher = mock(Matcher.class);

    private final ExceptionalBehaviourCriterion criterion = new ExceptionalBehaviourCriterion(matcher);

    @Test
    public void nonBehavioralExpectationShouldNotBeSupported() {
        final VerificationResult result = criterion.verify(mock(Expectation.class));

        assertEquals(VerificationOutcome.NOT_SUPPORTED, result.getOutcome());
    }

    @Test
    public void validExpectationShouldBeVerifiedCorrectly() {
        final RuntimeException expectedException = new RuntimeException();
        final BehaviouralExpectation expectation = BehaviouralExpectation.builder()
                .expectationReference(mock(ExpectationReference.class))
                .subject(mock(Procedure.class))
                .outcome(BehavioralOutcome.exceptional(expectedException))
                .criterion(criterion)
                .build();


        when(matcher.matches(eq(expectedException))).thenReturn(true);

        final VerificationResult result = criterion.verify(expectation);
        assertEquals(VerificationOutcome.VALID, result.getOutcome());
    }

    @Test
    public void invalidExceptionShouldNotBeMatchedCorrectly() {
        final RuntimeException expectedException = new RuntimeException();
        final BehaviouralExpectation expectation = BehaviouralExpectation.builder()
                .expectationReference(mock(ExpectationReference.class))
                .subject(mock(Procedure.class))
                .outcome(BehavioralOutcome.exceptional(expectedException))
                .criterion(criterion)
                .build();


        when(matcher.matches(eq(expectedException))).thenReturn(false);

        final VerificationResult result = criterion.verify(expectation);
        assertEquals(VerificationOutcome.INVALID, result.getOutcome());
    }

    @Test
    public void nonExceptionalOutcomeShouldNotBeValidated() {
        final BehaviouralExpectation expectation = BehaviouralExpectation.builder()
                .expectationReference(mock(ExpectationReference.class))
                .subject(mock(Procedure.class))
                .outcome(BehavioralOutcome.successful())
                .criterion(criterion)
                .build();

        final VerificationResult result = criterion.verify(expectation);
        assertEquals(VerificationOutcome.INVALID, result.getOutcome());
    }
}