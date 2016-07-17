package org.testifj.framework;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class BehavioralOutcomeTest {

    @Test
    public void exceptionalOutcomeCanBeCreated() {
        final RuntimeException exception = new RuntimeException();
        final BehavioralOutcome<RuntimeException> outcome = BehavioralOutcome.exceptional(exception);

        assertEquals(Optional.of(exception), outcome.getResult());
        assertTrue(outcome.isExceptional());
    }

    @Test
    public void nonExceptionalOutcomeCanBeCreated() {
        final BehavioralOutcome<String> outcome = BehavioralOutcome.successful("foo");

        assertEquals(Optional.of("foo"), outcome.getResult());;
        assertFalse(outcome.isExceptional());
    }

    @Test
    public void nonExceptionalOutcomeWithVoidReturnCanBeCreated() {
        final BehavioralOutcome<Void> outcome = BehavioralOutcome.successful();

        assertFalse(outcome.getResult().isPresent());
        assertFalse(outcome.isExceptional());
    }
}