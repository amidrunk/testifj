package org.testifj.delegate;

import org.junit.Test;
import org.testifj.Action;
import io.recode.Caller;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

@SuppressWarnings("unchecked")
public class GivenThenExpectationTest {

    private final Action exampleAction = mock(Action.class);
    private final Caller exampleCaller = new Caller(Arrays.asList(Thread.currentThread().getStackTrace()), 0);
    private final GivenThenExpectation exampleExpectation = new GivenThenExpectation(exampleCaller, "foo", exampleAction);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new GivenThenExpectation(null, "foo", exampleAction)).toThrow(AssertionError.class);
        expect(() -> new GivenThenExpectation(exampleCaller, "foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        given(exampleExpectation).then(it -> {
            expect(it.getCaller()).toBe(exampleCaller);
            expect(it.getProvidedValue()).toBe("foo");
            expect(it.getVerificationAction()).toBe(exampleAction);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleExpectation).toBe(equalTo(exampleExpectation));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleExpectation).not().toBe(equalTo(null));
        expect((Object) exampleExpectation).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final GivenThenExpectation other = new GivenThenExpectation(exampleCaller, "foo", exampleAction);

        expect(exampleExpectation).toBe(equalTo(other));
        expect(exampleExpectation.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleExpectation.toString()).then(it -> {
            expect(it).to(containString(exampleCaller.toString()));
            expect(it).to(containString("foo"));
            expect(it).to(containString(exampleAction.toString()));
        });
    }

}
