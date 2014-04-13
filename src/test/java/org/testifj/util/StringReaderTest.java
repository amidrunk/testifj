package org.testifj.util;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class StringReaderTest {

    @Test
    public void constructorShouldNotAcceptNullString() {
        expect(() -> new StringReader(null)).toThrow(AssertionError.class);
    }

    @Test
    public void remainderShouldInitiallyBeEntireString() {
        expect(new StringReader("foo").remainder()).to(equal("foo"));
    }

    @Test
    public void readExactStringShouldNotAcceptNullArgument() {
        expect(() -> new StringReader("foo").read(null)).toThrow(AssertionError.class);
    }

    @Test
    public void readExactStringShouldReturnFalseIfStringDoesNotMatcher() {
        final StringReader reader = new StringReader("foo");

        expect(reader.read("bar")).to(equal(false));
        expect(reader.remainder()).to(equal("foo"));
    }

    @Test
    public void readExactStringShouldReturnTrueAndForwardReaderIfStringMatches() {
        final StringReader reader = new StringReader("foobar");

        expect(reader.read("foo")).toBe(true);
        expect(reader.remainder()).toBe("bar");
    }

    @Test
    public void readUntilShouldNotAcceptNullPattern() {
        expect(() -> new StringReader("foo").readUntil(null)).toThrow(AssertionError.class);
    }

    @Test
    public void readUntilShouldReturnNullIfNoMatchIsFound() {
        final StringReader reader = new StringReader("foobar");

        expect(reader.readUntil(Pattern.compile("X")).isPresent()).toBe(false);
    }

    @Test
    public void readUntilShouldReturnMatchingStringAndProgressReader() {
        final StringReader reader = new StringReader("foo.bar");

        expect(reader.readUntil(Pattern.compile("\\.")).get()).toBe("foo");
        expect(reader.remainder()).toBe(".bar");
    }

    @Test
    public void readCharShouldReturnNegativeIfNothingRemains() {
        expect(new StringReader("").read()).toBe(-1);
    }

    @Test
    public void readCharShouldReturnCharacterAndProgressReader() {
        final StringReader reader = new StringReader("foo");

        expect(reader.read()).toBe((int) 'f');
        expect(reader.remainder()).toBe("oo");
        expect(reader.read()).toBe((int) 'o');
        expect(reader.remainder()).toBe("o");
    }

    @Test
    public void peekShouldReturnNegativeIfNothingRemains() {
        expect(new StringReader("").peek()).toBe(-1);
    }

    @Test
    public void peekShouldReturnCurrentCharAndNotProgressReader() {
        final StringReader reader = new StringReader("foo");

        expect(reader.peek()).toBe((int) 'f');
        expect(reader.remainder()).toBe("foo");
    }

    @Test
    public void skipShouldNotAcceptNegativeOrZeroDelta() {
        expect(() -> new StringReader("foo").skip(-1)).toThrow(AssertionError.class);
        expect(() -> new StringReader("foo").skip(0)).toThrow(AssertionError.class);
    }

    @Test
    public void skipShouldReturnFalseIfCountIsToLarge() {
        final StringReader reader = new StringReader("foo");

        expect(reader.skip(5)).toBe(false);
        expect(reader.remainder()).toBe("foo");
    }

    @Test
    public void skipShouldReturnTrueAndProgressReaderIfCountIsValid() {
        final StringReader reader = new StringReader("foo");

        expect(reader.skip(1)).toBe(true);
        expect(reader.remainder()).toBe("oo");
    }


}
