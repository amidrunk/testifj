package org.testifj.framework;

import org.junit.Test;
import org.testifj.Procedure;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BehaviouralExpectationTest {

    @Test
    public void behavioralExpectationCanBeBuilt() {
        final Procedure procedure = mock(Procedure.class);
        final ExpectationReference expectationReference = mock(ExpectationReference.class);
        final BehaviouralCriterion criterion = mock(BehaviouralCriterion.class);
        final BehavioralOutcome outcome = mock(BehavioralOutcome.class);

        final BehaviouralExpectation expectation = BehaviouralExpectation.builder()
                .subject(procedure)
                .expectationReference(expectationReference)
                .criterion(criterion)
                .outcome(outcome)
                .build();

        assertEquals(procedure, expectation.getSubject());
        assertEquals(expectationReference, expectation.getExpectationReference());
        assertEquals(criterion, expectation.getCriterion());
        assertEquals(outcome, expectation.getOutcome());
    }
}