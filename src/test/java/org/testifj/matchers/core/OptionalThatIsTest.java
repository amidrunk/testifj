package org.testifj.matchers.core;

import org.junit.Test;

import java.util.Optional;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class OptionalThatIsTest {

    @Test
    public void presentMatcherShouldMatchPresentOptional() {
        expect(present().matches(Optional.of("foo"))).toBe(true);
        expect(Optional.of("foo")).toBe(present());
    }

    @Test
    public void presentMatcherShouldNotMatchNonPresentOptional() {
        expect(present().matches(Optional.empty())).toBe(false);
        expect(() -> expect(Optional.empty()).toBe(present())).toThrow(AssertionError.class);
    }

    @Test
    public void presentMatcherCanBeInvertedToMatchNonPresentOptional() {
        expect(Optional.empty()).not().toBe(present());
    }

    @Test
    public void presentMatcherCanBeInvertedToNotMatchPresentOptional() {
        expect(() -> expect(Optional.of("foo")).not().toBe(present()));
    }

    @Test
    public void optionalOfShouldNotAcceptNullArg() {
        expect(() -> OptionalThatIs.optionalOf(null)).toThrow(AssertionError.class);
    }

    @Test
    public void optionalOfShouldNotMatchEmptyOptional() {
        expect(OptionalThatIs.optionalOf("foo").matches(Optional.<String>empty())).toBe(false);
    }

    @Test
    public void optionalOfShouldNotMatchOptionalOfDifferentInstance() {
        expect(OptionalThatIs.optionalOf("foo").matches(Optional.of("bar"))).toBe(false);
    }

    @Test
    public void optionalOfShouldMatchOptionalOfEqualInstance() {
        expect(OptionalThatIs.optionalOf("foo").matches(Optional.of("foo"))).toBe(true);
    }

    @Test
    public void optionalOfMatcherShouldNotMatchNull() {
        expect(OptionalThatIs.optionalOf("foo").matches(null)).toBe(false);
    }

}
