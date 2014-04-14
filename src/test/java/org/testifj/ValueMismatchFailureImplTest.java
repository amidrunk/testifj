package org.testifj;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ValueMismatchFailureImplTest {

    private final Matcher<?> matcher = Mockito.mock(Matcher.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new ValueMismatchFailureImpl(null, matcher, Optional.empty(), "foo")).toThrow(AssertionError.class);
        expect(() -> new ValueMismatchFailureImpl(new StackTraceElement[0], null, Optional.empty(), "foo")).toThrow(AssertionError.class);
        expect(() -> new ValueMismatchFailureImpl(new StackTraceElement[0], null, null, "foo")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final Optional<Object> expectedValue = Optional.of("bar");
        final StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        final ValueMismatchFailureImpl failure = new ValueMismatchFailureImpl(callStack, matcher, expectedValue, "foo");

        expect(failure.getCallStack().toArray()).toBe(callStack);
        expect((Object) failure.getMatcher()).toBe(equalTo(matcher));
        expect(failure.getValue()).toBe("foo");
        expect(failure.getExpectedValue()).toBe(expectedValue);
    }

}
