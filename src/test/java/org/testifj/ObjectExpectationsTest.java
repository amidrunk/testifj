package org.testifj;

import org.junit.After;
import org.junit.Test;
import org.testifj.framework.*;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.testifj.Expectations.expect;

public class ObjectExpectationsTest {

    private RecordingTestContextProvider testContextProvider = (RecordingTestContextProvider) TestContextProviders.configuredTestContextProvider();

    @After
    public void tearDown() {
        testContextProvider.reset();
    }

    @Test
    public void itShouldBePossibleToExpectAnObjectToBeNull() {
        final Object anObject = new Object();
        expect(anObject).toBeNull();

        final ValueExpectation expectation = validValueExpectation(-2);

        assertSame(anObject, expectation.getSubject());
        assertEquals(VerificationOutcome.INVALID, expectation.getCriterion().verify(expectation).getOutcome());
        assertFalse(expectation.getExpectedValue().isPresent());
    }

    @Test
    public void itShouldBePossibleToExpectAnObjectNotToBeNull() {
        final Object anObject = new Object();
        expect(anObject).not().toBeNull();

        final ValueExpectation expectation = validValueExpectation(-2);

        assertSame(anObject, expectation.getSubject());
        assertEquals(VerificationOutcome.VALID, expectation.getCriterion().verify(expectation).getOutcome());
        assertFalse(expectation.getExpectedValue().isPresent());
    }

    @Test
    public void itShouldBePossibleToExpectAnObjectToBeEqualToAnotherObjectWithMatch() {
        final String object1 = "foo";
        final String object2 = "foo";

        expect(object1).toEqual(object2);

        final ValueExpectation expectation = validValueExpectation(-2);

        assertSame(object1, expectation.getSubject());
        assertEquals(Optional.of(object2), expectation.getExpectedValue());
        assertEquals(VerificationOutcome.VALID, expectation.getCriterion().verify(expectation).getOutcome());
    }

    @Test
    public void itShouldBePossibleToExpectAnObjectToBeEqualToAnotherObjectWithNoMatch() {
        final String object1 = "foo";
        final String object2 = "bar";

        expect(object1).toEqual(object2);

        final ValueExpectation expectation = validValueExpectation(-2);

        assertSame(object1, expectation.getSubject());
        assertEquals(Optional.of(object2), expectation.getExpectedValue());
        assertEquals(VerificationOutcome.INVALID, expectation.getCriterion().verify(expectation).getOutcome());
    }

    @Test
    public void itShouldBePossibleToExpectAnObjectNotToBeEqualToAnotherObjectWithMatch() {
        final String object1 = "foo";
        final String object2 = "bar";

        expect(object1).not().toEqual(object2);

        final ValueExpectation expectation = validValueExpectation(-2);

        assertSame(object1, expectation.getSubject());
        assertEquals(Optional.of(object2), expectation.getExpectedValue());
        assertEquals(VerificationOutcome.VALID, expectation.getCriterion().verify(expectation).getOutcome());
    }

    @Test
    public void itShouldBePossibleToExpectAnObjectNotToBeEqualToAnotherObjectWithNoMatch() {
        final String object1 = "foo";
        final String object2 = "foo";

        expect(object1).not().toEqual(object2);

        final ValueExpectation expectation = validValueExpectation(-2);

        assertSame(object1, expectation.getSubject());
        assertEquals(Optional.of(object2), expectation.getExpectedValue());
        assertEquals(VerificationOutcome.INVALID, expectation.getCriterion().verify(expectation).getOutcome());
    }

    private ValueExpectation validValueExpectation(int expectationOffset) {
        final Expectation<?> expectation = testContextProvider.expectations().take().orElseThrow(AssertionError::new);
        assertTrue(expectation instanceof ValueExpectation);

        final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        final ValueExpectation valueExpectation = (ValueExpectation) expectation;
        final InlineExpectationReference reference = valueExpectation.getExpectationReference().as(InlineExpectationReference.class).get();

        assertEquals(stackTrace.getMethodName(), reference.getMethodName());
        assertEquals(stackTrace.getLineNumber() + expectationOffset, reference.getLineNumber());

        return (ValueExpectation) expectation;
    }
}
