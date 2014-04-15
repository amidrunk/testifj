package org.testifj.matchers.core;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ExceptionWhereTest {

    @Test
    public void messageWithSpecificationShouldMatchExceptionWithMatchingMessage() {
        final RuntimeException e = new RuntimeException("foo");
        expect(ExceptionWhere.messageIs(equalTo("foo")).matches(e)).toBe(true);
    }

    @Test
    public void messageWithSpecificationShouldNotMatchExceptionWithNonMatchingMessage() {
        final RuntimeException e = new RuntimeException("foo");
        expect(ExceptionWhere.messageIs(equalTo("bar")).matches(e)).toBe(false);
    }

    @Test
    public void exceptionCannotBeNull() {
        expect(() -> ExceptionWhere.messageIs(equalTo("foo")).matches(null)).toThrow(AssertionError.class);
    }

}
