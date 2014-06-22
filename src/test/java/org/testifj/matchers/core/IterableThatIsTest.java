package org.testifj.matchers.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;

public class IterableThatIsTest {

    @Test
    public void emptyIterableShouldNotMatchNonEmptyIterable() {
        expect(IterableThatIs.emptyIterable().matches(Arrays.asList("foo"))).toBe(false);
        expect(IterableThatIs.emptyIterable().matches(Arrays.asList("foo", "bar"))).toBe(false);
    }

    @Test
    public void emptyIterableShouldMatchEmptyIterable() {
        expect(IterableThatIs.emptyIterable().matches(Collections.emptyList())).toBe(true);
    }

    @Test
    public void iterableOfShouldNotMatchIterableWithDifferentContent() {
        expect(IterableThatIs.iterableOf("foo", "bar").matches(Arrays.asList("foo"))).toBe(false);
        expect(IterableThatIs.iterableOf("foo", "bar").matches(Arrays.asList("foo", "baz"))).toBe(false);
        expect(IterableThatIs.iterableOf("foo", "bar").matches(Arrays.asList("foo", "bar", "baz"))).toBe(false);
    }

    @Test
    public void iterableOfShouldMatchIterableWithEqualContent() {
        expect(IterableThatIs.iterableOf().matches(Collections.emptyList())).toBe(true);
        expect(IterableThatIs.iterableOf("foo", "bar").matches(Arrays.asList("foo", "bar"))).toBe(true);
    }

    @Test
    public void iterableOfShouldNotAcceptNullArg() {
        expect(() -> IterableThatIs.<Object, Iterable<Object>>iterableOf((Object[]) null)).toThrow(AssertionError.class);
    }

}