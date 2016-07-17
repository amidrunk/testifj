package org.testifj.framework;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class ExpectationContextTest {


    @Test
    public void expectationContextWithSubjectCanBeCreated() {
        final ExpectationReference reference = mock(ExpectationReference.class);
        final ExpectationContext<String> context = new ExpectationContext<>("foo", reference);

        assertEquals("foo", context.getSubject());
        assertEquals(reference, context.getExpectationReference());
    }

    @Test
    public void expectationContextWithNullSubjectCanBeCreated() {
        final ExpectationReference reference = mock(ExpectationReference.class);
        final ExpectationContext<String> context = new ExpectationContext<>(null, reference);

        assertNull(context.getSubject());
        assertEquals(reference, context.getExpectationReference());
    }
}