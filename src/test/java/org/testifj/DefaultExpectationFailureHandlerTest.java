package org.testifj;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ExceptionWhere.messageIs;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class DefaultExpectationFailureHandlerTest {

    private final ExpectationFailureHandler handler = new DefaultExpectationFailureHandler.Builder().build();
    private final Matcher matcher = mock(Matcher.class);

    @Test
    public void builderShouldNotAcceptNullDependencies() {
        final DefaultExpectationFailureHandler.Builder builder = new DefaultExpectationFailureHandler.Builder();

        expect(() -> builder.setDecompiler(null)).toThrow(AssertionError.class);
        expect(() -> builder.setClassFileReader(null)).toThrow(AssertionError.class);
        expect(() -> builder.setDescriptionFormat(null)).toThrow(AssertionError.class);
        expect(() -> builder.setSyntaxElementDescriber(null)).toThrow(AssertionError.class);
    }

    @Test
    public void builderShouldNotAcceptInvalidDependencies() {
        final DefaultExpectationFailureHandler.Builder builder = new DefaultExpectationFailureHandler.Builder();

        expect(() -> builder.setDecompiler(null)).toThrow(AssertionError.class);
        expect(() -> builder.setClassFileReader(null)).toThrow(AssertionError.class);
        expect(() -> builder.setSyntaxElementDescriber(null)).toThrow(AssertionError.class);
    }

    @Test
    public void failureWithExpectedValueCanBeDescribed() {
        expect(getExampleString()).toBe("foo");

        final Caller caller = caller(-2);

        expect(() -> handler.handleExpectationFailure(failure(caller, "foo")))
                .toThrow(AssertionError.class)
                .where(messageIs(equalTo("Expected getExampleString() => \"foo\" to be \"foo\"")));
    }

    @Test
    public void failureWithExpectationExpressedThroughMatcherWithoutExpectedValueCanBeDescribed() {
        expect("foo").toBe(equalTo("foo"));

        final Caller caller = caller(-2);

        expect(() -> handler.handleExpectationFailure(failure(caller, "foo")))
                .toThrow(AssertionError.class)
                .where(messageIs(equalTo("Expected \"foo\" to be equalTo(\"foo\")")));
    }

    @Test
    public void invertedExpectationExpressedThroughValuesCanBeDescribed() {
        expect("foo").not().toBe("bar");

        final Caller caller = caller(-2);

        expect(() -> handler.handleExpectationFailure(failure(caller, Optional.of("bar"), "foo")))
                .toThrow(AssertionError.class)
                .where(messageIs(equalTo("Expected \"foo\" not to be \"bar\"")));
    }

    @Test
    public void expectationWithLambdaOnConstantCanBeDescribed() {
        expect("foo").toBe(s -> s.contains("foo"));

        final Caller caller = caller(-2);

        expect(() -> handler.handleExpectationFailure(failure(caller, Optional.empty(), "foo")))
                .toThrow(AssertionError.class)
                .where(messageIs(equalTo("Expected \"foo\" to be s -> s.contains(\"foo\")")));
    }

    @Test
    @Ignore("Reintroduce and fix later")
    public void expectedExceptionFailureShouldDescribeProcedureAndExpectedExpression() {
        boolean failed = false;

        try {
            expect(this::doStuff).toThrow(RuntimeException.class);
        } catch (AssertionError e) {
            failed = true;
            expect(e.getMessage()).toBe(
                "Expected \"doStuff()\" to throw java.lang.RuntimeException");
        }

        expect(failed).toBe(true);
    }

    private ValueMismatchFailureImpl failure(Caller caller, Object actualValue) {
        return failure(caller, Optional.empty(), actualValue);
    }

    private ValueMismatchFailureImpl failure(Caller caller, Optional<Object> expectedValue, Object actualValue) {
        return new ValueMismatchFailureImpl(caller, matcher, expectedValue, actualValue);
    }

    private Caller caller(int offset) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final int callerStackIndex = 2;

        stackTrace[callerStackIndex] = ClassModelTestUtils.offset(stackTrace[callerStackIndex], offset);

        return new Caller(Arrays.asList(stackTrace), callerStackIndex);
    }

    private String getExampleString() {
        return "foo";
    }

    private void doStuff() {

    }

}
