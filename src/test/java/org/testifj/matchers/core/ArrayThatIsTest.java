package org.testifj.matchers.core;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class ArrayThatIsTest {

    @Test
    public void arrayOfShouldNotAcceptNullExpectedArray() {
        expect(() -> ArrayThatIs.<Object>arrayOf((Object[]) null)).toThrow(AssertionError.class);
    }

    @Test
    public void arrayOfShouldNotMatchArrayWithDifferentContents() {
        expect(ArrayThatIs.arrayOf("foo", "bar").matches(new String[]{"foo"})).toBe(false);
        expect(ArrayThatIs.arrayOf("foo", "bar").matches(new String[]{"foo", "baz"})).toBe(false);
        expect(ArrayThatIs.arrayOf("foo", "bar").matches(new String[]{"foo", "bar", "baz"})).toBe(false);
    }

    @Test
    public void arrayOfShouldMatchArrayWithEqualContents() {
        expect(ArrayThatIs.arrayOf("foo").matches(new String[]{"foo"})).toBe(true);
        expect(ArrayThatIs.arrayOf("foo", "bar").matches(new String[]{"foo", "bar"})).toBe(true);
        expect(ArrayThatIs.arrayOf("foo", "bar", "baz").matches(new String[]{"foo", "bar", "baz"})).toBe(true);
    }

    @Test
    public void intArrayOfShouldNotAcceptNullExpectedArray() {
        expect(() -> ArrayThatIs.arrayOf((int[]) null)).toThrow(AssertionError.class);
    }

    @Test
    public void intArrayOfShouldNotMatchArrayWithDifferentContents() {
        expect(ArrayThatIs.<int[]>arrayOf(1, 2, 3).matches(new int[]{1})).toBe(false);
        expect(ArrayThatIs.<int[]>arrayOf(1, 2, 3).matches(new int[]{1, 3, 3})).toBe(false);
        expect(ArrayThatIs.<int[]>arrayOf(1, 2, 3).matches(new int[]{1, 2, 2})).toBe(false);
        expect(ArrayThatIs.<int[]>arrayOf(1, 2, 3).matches(new int[]{1, 2, 3, 4})).toBe(false);
    }

    @Test
    public void intArrayOfShouldMatchArrayWithEqualContents() {
        expect(ArrayThatIs.<int[]>arrayOf(new int[0]).matches(new int[]{})).toBe(true);
        expect(ArrayThatIs.<int[]>arrayOf(1).matches(new int[]{1})).toBe(true);
        expect(ArrayThatIs.<int[]>arrayOf(1, 2).matches(new int[]{1, 2})).toBe(true);
        expect(ArrayThatIs.<int[]>arrayOf(1, 2, 3).matches(new int[]{1, 2, 3})).toBe(true);
    }

    @Test
    public void intArrayWithShouldNotAcceptNullExpectedElements() {
        expect(() -> ArrayThatIs.arrayWith((int[]) null)).toThrow(AssertionError.class);
    }

    @Test
    public void intArrayWithShouldNotMatchWhenExpectedArrayDoesNotContainAllElements() {
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{1})).toBe(false);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{1, 2})).toBe(false);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{1, 2, 4})).toBe(false);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{1, 2, 4, 5})).toBe(false);
    }

    @Test
    public void intArrayWithShouldMatchArrayThatContainsAllExpectedElements() {
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{1, 2, 3})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{2, 1, 3})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{1, 3, 2})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{0, 1, 2, 3})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{0, 2, 1, 3})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{1, 2, 3, 0})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{1, 3, 2, 0})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{0, 1, 2, 3, 0})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{0, 2, 1, 3, 0})).toBe(true);
        expect(ArrayThatIs.arrayWith(1, 2, 3).matches(new int[]{0, 3, 1, 2, 0})).toBe(true);
    }

    @Test
    public void ofLengthShouldFailIfLengthIsNegative() {
        expect(() -> ArrayThatIs.ofLength(-1)).toThrow(AssertionError.class);
    }

    @Test
    public void ofLengthShouldNotMatchArrayWithDifferentLength() {
        expect(ArrayThatIs.ofLength(0).matches(new Object[1])).toBe(false);
        expect(ArrayThatIs.ofLength(1).matches(new Object[0])).toBe(false);
        expect(ArrayThatIs.ofLength(1).matches(new Object[2])).toBe(false);
        expect(ArrayThatIs.ofLength(2).matches(new Object[0])).toBe(false);
        expect(ArrayThatIs.ofLength(2).matches(new Object[1])).toBe(false);
        expect(ArrayThatIs.ofLength(2).matches(new Object[3])).toBe(false);
    }

    @Test
    public void ofLengthShouldMatchArrayWithEqualLength() {
        expect(ArrayThatIs.ofLength(0).matches(new Object[0])).toBe(true);
        expect(ArrayThatIs.ofLength(1).matches(new Object[1])).toBe(true);
        expect(ArrayThatIs.ofLength(2).matches(new Object[2])).toBe(true);
        expect(ArrayThatIs.ofLength(3).matches(new Object[3])).toBe(true);
    }
}