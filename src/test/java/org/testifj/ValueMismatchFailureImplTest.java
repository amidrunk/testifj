package org.testifj;

import org.junit.Test;
import org.mockito.Mockito;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ValueMismatchFailureImplTest {

    private final Matcher<?> matcher = Mockito.mock(Matcher.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new ValueMismatchFailureImpl(null, matcher, "foo")).toThrow(AssertionError.class);
        expect(() -> new ValueMismatchFailureImpl(new StackTraceElement[0], null, "foo")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        final ValueMismatchFailureImpl failure = new ValueMismatchFailureImpl(callStack, matcher, "foo");

        expect(failure.getCallStack().toArray()).toBe(callStack);
        expect((Object) failure.getMatcher()).toBe(equalTo(matcher));
        expect(failure.getValue()).toBe("foo");
    }

}
