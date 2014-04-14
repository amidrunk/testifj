package org.testifj;

import org.junit.Test;

import java.util.Arrays;

import static org.testifj.Expect.expect;

public class CallerTest {

    @Test
    public void constructorShouldValidateParameters() {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        expect(() -> new Caller(null, 0)).toThrow(AssertionError.class);
        expect(() -> new Caller(Arrays.asList(elements), -1)).toThrow(AssertionError.class);
        expect(() -> new Caller(Arrays.asList(elements), elements.length)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final Caller caller = new Caller(Arrays.asList(stackTrace), 1);

        expect(caller.getCallStack().toArray()).toBe(stackTrace);
        expect(caller.getCallerStackTraceElement()).toBe(stackTrace[1]);
    }

}
