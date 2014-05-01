package org.testifj.util;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class Arrays2Test {

    @Test
    public void singleShouldNotAcceptNullArray() {
        expect(() -> Arrays2.single(null)).toThrow(AssertionError.class);
    }

    @Test
    public void singleShouldFailForEmptyArray() {
        expect(() -> Arrays2.single(new Object[0])).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void singleShouldFailForArrayWithMoreThanOneElement() {
        expect(() -> Arrays2.single(new Object[]{"foo", "bar"})).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void singleShouldReturnOnlyArrayElement() {
        expect(Arrays2.single(new Object[]{"foo"})).toBe("foo");
    }

    @Test
    public void singleWithFunctionShouldNotAcceptNullFunction() {
        expect(() -> Arrays2.single(new Object[0], null)).toThrow(AssertionError.class);
    }

    @Test
    public void singleWithFunctionShouldReturnSingleTransformedArguments() {
        expect(Arrays2.single(new String[]{"foo"}, String::length)).toBe(3);
    }
}
