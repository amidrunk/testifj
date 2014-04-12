package org.testifj;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OutcomeTest {

    @Test(expected = AssertionError.class)
    public void exceptionalOutcomeCannotHaveNullException() {
        Outcome.exceptional(null);
    }

    @Test
    public void exceptionalOutcomeShouldBeExceptionalAndHaveException() {
        final Exception cause = new Exception();
        final Outcome outcome = Outcome.exceptional(cause);

        assertTrue(outcome.isExceptional());
        assertEquals(cause, outcome.getException());
    }

    @Test
    public void successfulOutcomeShouldNotBeExceptional() {
        assertFalse(Outcome.successful().isExceptional());
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionCannotBeAccessedOnSuccessfulOutcome() {
        Outcome.successful().getException();
    }

}
