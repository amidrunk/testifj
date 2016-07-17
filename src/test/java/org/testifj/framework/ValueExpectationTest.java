package org.testifj.framework;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class ValueExpectationTest {

    private final Criterion criterion = mock(Criterion.class);

    private final ExpectationReference expectationReference = mock(ExpectationReference.class);

    @Test
    public void expectationWithoutExpectedValueCanBeCreated() {
        final ValueExpectation expectation = ValueExpectation.builder()
                .criterion(criterion)
                .expectationReference(expectationReference)
                .subject("foo")
                .build();

        assertEquals(criterion, expectation.getCriterion());
        assertEquals(expectationReference, expectation.getExpectationReference());
        assertEquals("foo", expectation.getSubject());
        assertFalse(expectation.getExpectedValue().isPresent());
    }

    @Test
    public void expectationWithExpectedValueCanBeCreated() {
        final ValueExpectation expectation = ValueExpectation.builder()
                .criterion(criterion)
                .expectationReference(expectationReference)
                .subject("foo")
                .expectedValue("bar")
                .build();

        assertEquals(criterion, expectation.getCriterion());
        assertEquals(expectationReference, expectation.getExpectationReference());
        assertEquals("foo", expectation.getSubject());
        assertEquals(Optional.of("bar"), expectation.getExpectedValue());
    }
}