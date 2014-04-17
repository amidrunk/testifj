package org.testifj.impl;

import org.junit.Test;
import org.testifj.Caller;

import java.util.Arrays;

import static org.testifj.Expect.expect;

public class ExpectedExceptionNotThrownImplTest {

    private final Caller exampleCaller = new Caller(Arrays.asList(Thread.currentThread().getStackTrace()), 0);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new ExpectedExceptionNotThrownImpl(null, RuntimeException.class)).toThrow(AssertionError.class);
        expect(() -> new ExpectedExceptionNotThrownImpl(exampleCaller, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final ExpectedExceptionNotThrownImpl expectedExceptionNotThrown = new ExpectedExceptionNotThrownImpl(exampleCaller, RuntimeException.class);

        expect(expectedExceptionNotThrown.getCaller()).toBe(exampleCaller);
        expect(expectedExceptionNotThrown.getExpectedException()).to(e -> e.equals(RuntimeException.class));
    }

}
