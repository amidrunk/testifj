package org.testifj.framework;

import io.recode.CodeLocation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InlineExpectationReferenceTest {

    @Test
    public void referenceCanBeCreatedFromStackTrace() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final InlineExpectationReference reference = InlineExpectationReference.create(stackTrace, 1);

        assertEquals("org.testifj.framework.InlineExpectationReferenceTest", reference.getClassName());
        assertEquals("referenceCanBeCreatedFromStackTrace", reference.getMethodName());
        assertEquals("InlineExpectationReferenceTest.java", reference.getFileName());
        assertEquals(Thread.currentThread().getStackTrace()[1].getLineNumber() - 6, reference.getLineNumber());
    }

    @Test
    public void codeLocationCanBeRetrievedFromReference() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final InlineExpectationReference reference = InlineExpectationReference.create(stackTrace, 1);
        final CodeLocation location = reference.toCodeLocation();

        assertEquals("org.testifj.framework.InlineExpectationReferenceTest", location.getClassName());
        assertEquals("codeLocationCanBeRetrievedFromReference", location.getMethodName());
        assertEquals("InlineExpectationReferenceTest.java", location.getFileName());
        assertEquals(Thread.currentThread().getStackTrace()[1].getLineNumber() - 6, location.getLineNumber());
    }

}