package org.testifj.framework;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpectationReferenceTest {

    @Test
    public void expectationReferenceCanBeCastToCorrectType() {
        final ExpectationReference reference = InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1);

        assertEquals(Optional.of(reference), reference.as(InlineExpectationReference.class));
    }

    @Test
    public void expectationReferenceCannotBeCastToIncorrectType() {
        final ExpectationReference reference = new ExpectationReference() {};

        assertFalse(reference.as(InlineExpectationReference.class).isPresent());
    }
}