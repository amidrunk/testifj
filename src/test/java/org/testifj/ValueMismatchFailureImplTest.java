package org.testifj;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ValueMismatchFailureImplTest {

    private final Matcher<?> matcher = Mockito.mock(Matcher.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        final Caller caller = new Caller(Arrays.asList(Thread.currentThread().getStackTrace()), 0);

        expect(() -> new ValueMismatchFailureImpl(null, matcher, Optional.empty(), "foo")).toThrow(AssertionError.class);
        expect(() -> new ValueMismatchFailureImpl(caller, null, Optional.empty(), "foo")).toThrow(AssertionError.class);
        expect(() -> new ValueMismatchFailureImpl(caller, null, null, "foo")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final Optional<Object> expectedValue = Optional.of("bar");
        final StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        final Caller caller = new Caller(Arrays.asList(callStack), 0);
        final ValueMismatchFailureImpl failure = new ValueMismatchFailureImpl(caller, matcher, expectedValue, "foo");

        expect(failure.getCaller()).toBe(caller);
        expect((Object) failure.getMatcher()).toBe(equalTo(matcher));
        expect(failure.getValue()).toBe("foo");
        expect(failure.getExpectedValue()).toBe(expectedValue);
    }

}
