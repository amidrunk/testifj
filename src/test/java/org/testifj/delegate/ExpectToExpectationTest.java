package org.testifj.delegate;

import org.junit.Test;
import io.recode.Caller;
import org.testifj.Matcher;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.OptionalThatIs.present;
import static org.testifj.matchers.core.StringShould.containString;

public class ExpectToExpectationTest {

    private final Matcher matcher = mock(Matcher.class);
    private final Caller caller = new Caller(Arrays.asList(Thread.currentThread().getStackTrace()), 0);
    private final ExpectToExpectation exampleExpectation = new ExpectToExpectation(caller, "foo", Optional.of("bar"), matcher);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new ExpectToExpectation(null, "foo", Optional.empty(), matcher)).toThrow(AssertionError.class);
        expect(() -> new ExpectToExpectation(caller, "foo", null, matcher)).toThrow(AssertionError.class);
        expect(() -> new ExpectToExpectation(caller, "foo", Optional.empty(), null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        given(exampleExpectation).then(it -> {
            expect(it.getCaller()).toBe(caller);
            expect(it.getActualValue()).toBe("foo");
            expect(it.getExpectedValue()).toBe(present());
            expect(it.getExpectedValue().get()).toBe("bar");
            expect(it.getMatcher()).toBe(equalTo(matcher));
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
        final ExpectToExpectation other = new ExpectToExpectation(caller, "foo", Optional.of("bar"), matcher);

        expect(exampleExpectation).toBe(equalTo(other));
        expect(exampleExpectation.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleExpectation.toString()).then(it -> {
            expect(it).to(containString(caller.toString()));
            expect(it).to(containString("foo"));
            expect(it).to(containString("bar"));
            expect(it).to(containString(matcher.toString()));
        });
    }

}
