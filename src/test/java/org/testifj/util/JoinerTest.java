package org.testifj.util;

import org.junit.Test;

import java.util.Arrays;

import static org.testifj.Expect.expect;

public class JoinerTest {

    @Test
    public void joinShouldNotAcceptNullArgument() {
        expect(() -> Joiner.join(null)).toThrow(AssertionError.class);
    }

    @Test
    public void joinContinuationShouldNotAcceptNullSeparator() {
        expect(() -> Joiner.join(Arrays.asList("foo")).on(null)).toThrow(AssertionError.class);
    }

    @Test
    public void joinerShouldJoinElementsOnStringValue() {
        expect(Joiner.join(Arrays.asList("foo", "bar", "baz")).on(", ")).toBe("foo, bar, baz");
    }

}
