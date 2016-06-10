package org.testifj;

import io.recode.Caller;
import org.junit.Test;

import java.util.Arrays;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class OutcomeTest {

    private final Caller caller = new Caller(Arrays.asList(Thread.currentThread().getStackTrace()), 0);

    @Test
    public void exceptionalOutcomeMustHaveCallerAndExpectedException() {
        expect(() -> Outcome.exceptional(null, new RuntimeException())).toThrow(AssertionError.class);
        expect(() -> Outcome.exceptional(caller, null)).toThrow(AssertionError.class);
    }

    @Test
    public void exceptionalOutcomeShouldBeExceptionalAndHaveExceptionAndHaveCaller() {
        final Exception cause = new Exception();
        final Outcome outcome = Outcome.exceptional(caller, cause);

        expect(outcome.isExceptional()).toBe(true);
        expect(outcome.getException()).toBe(present());
        expect(outcome.getException().get()).toBe(cause);
        expect(outcome.getCaller()).toBe(caller);
    }

    @Test
    public void successfulOutcomeCannotHaveNullCaller() {
        expect(() -> Outcome.successful(null)).toThrow(AssertionError.class);
    }

    @Test
    public void successfulOutcomeShouldNotBeExceptional() {
        expect(Outcome.successful(caller).isExceptional()).toBe(false);
    }

    @Test
    public void successfulOutcomeShouldHaveCaller() {
        final Outcome outcome = Outcome.successful(caller);

        expect(outcome.getCaller()).toBe(caller);
    }

}
