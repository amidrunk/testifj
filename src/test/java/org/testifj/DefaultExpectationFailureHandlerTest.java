package org.testifj;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.matchers.core.Equal;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.fail;
import static org.testifj.Expect.expect;

public class DefaultExpectationFailureHandlerTest {

    private final ExpectationFailureHandler handler = new DefaultExpectationFailureHandler.Builder().build();

    @Test
    public void builderShouldNotAcceptInvalidDependencies() {
        final DefaultExpectationFailureHandler.Builder builder = new DefaultExpectationFailureHandler.Builder();

        expect(() -> builder.setByteCodeParser(null)).toThrow(AssertionError.class);
        expect(() -> builder.setClassFileReader(null)).toThrow(AssertionError.class);
        expect(() -> builder.setSyntaxElementDescriber(null)).toThrow(AssertionError.class);
    }

    @Test
    public void failureWithExpectedInstanceShouldGetDescribed() {
        Caller caller = null;

        try {
            expect(getExampleString()).toBe("bar");
            fail();
        } catch (AssertionError e) {
            caller = new Caller(Arrays.asList(e.getStackTrace()), 2);
            e.printStackTrace();
        }

        final ExpectationFailure failure = new ValueMismatchFailureImpl(caller, Equal.equal("bar"), Optional.of("bar"), "foo");

        // "Expected getExampleString() => "foo" to be "bar"
        expect(() -> handler.handleExpectationFailure(failure)).toThrow(AssertionError.class).where(e ->
            e.getMessage().contains("Expected getExampleString() => \"foo\" to be \"bar\"")
        );
    }

    private String getExampleString() {
        return "foo";
    }

}
