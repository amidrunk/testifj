package org.testifj.util;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class StringsTest {

    @Test
    public void rightPadShouldNotAcceptInvalidArguments() {
        expect(() -> Strings.rightPad(null, 10, ' ')).toThrow(AssertionError.class);
        expect(() -> Strings.rightPad("", -1, ' ')).toThrow(AssertionError.class);
    }

    @Test
    public void rightPadShouldReturnSameStringIfLengthIsEqualToOrGreaterThanRequired() {
        expect(Strings.rightPad("foo", 2, ' ')).toBe("foo");
        expect(Strings.rightPad("foo", 3, ' ')).toBe("foo");
    }

    @Test
    public void rightPadShouldAppendPadCharacterAndReturnStringOfRequiredLength() {
        expect(Strings.rightPad("foo", 4, ' ')).toBe("foo ");
        expect(Strings.rightPad("foo", 5, 'X')).toBe("fooXX");
        expect(Strings.rightPad("foo", 6, 'Y')).toBe("fooYYY");
    }

}
