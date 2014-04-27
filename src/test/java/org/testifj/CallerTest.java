package org.testifj;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.OptionalThatIs.present;

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

    @Test
    public void meShouldReturnReferenceToCallingLine() {
        given(Caller.me()).then(it -> {
            expect(it.getCallerStackTraceElement().getClassName()).toBe(getClass().getName());
            expect(it.getCallerStackTraceElement().getMethodName()).toBe("meShouldReturnReferenceToCallingLine");
        });
    }

    @Test
    public void adjacentShouldReturnCallerReferencingAdjacentCodeLine() {
        final Caller caller1 = Caller.me();
        final Caller caller2 = Caller.adjacent(-1);

        expect(caller2.getCallerStackTraceElement()).toBe(equalTo(caller1.getCallerStackTraceElement()));
    }

    @Test
    public void getCallerShouldReturnCallerOfCaller() {
        final Caller caller = createCaller();

        given(caller.getCaller()).then(it -> {
            expect(it.getCallerStackTraceElement().getClassName()).toBe(getClass().getName());
            expect(it.getCallerStackTraceElement().getMethodName()).toBe("getCallerShouldReturnCallerOfCaller");
        });
    }

    @Test
    public void scanShouldNotAcceptNullPredicate() {
        final Caller caller = Caller.me();

        expect(() -> caller.scan(null)).toThrow(AssertionError.class);
    }

    @Test
    public void scanShouldReturnNonPresentOptionalIfMatchIsNotFound() {
        final Caller caller = Caller.me();

        expect(caller.scan(e -> e.getMethodName().equals("invalid"))).not().toBe(present());
    }

    @Test
    public void scanShouldReturnFirstMatchingStackTraceElement() {
        final Caller caller = createCaller();
        final Optional<Caller> result = caller.scan(e -> e.getMethodName().equals("scanShouldReturnFirstMatchingStackTraceElement"));

        expect(result).toBe(present());
        expect(result.get().getCallerStackTraceElement().getMethodName()).toBe("scanShouldReturnFirstMatchingStackTraceElement");
    }

    private Caller createCaller() {
        return Caller.me();
    }


}
