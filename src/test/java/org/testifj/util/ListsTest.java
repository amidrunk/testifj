package org.testifj.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class ListsTest {

    @Test
    public void firstShouldNotAcceptNullList() {
        expect(() -> Lists.first(null)).toThrow(AssertionError.class);
    }

    @Test
    public void firstShouldReturnNonPresentOptionalForEmptyList() {
        expect(Lists.first(Collections.emptyList())).not().toBe(present());
    }

    @Test
    public void firstShouldReturnFirstElementInList() {
        expect(Lists.first(Arrays.asList("foo", "bar"))).toBe(optionalOf("foo"));
    }

    @Test
    public void lastShouldNotAcceptNullList() {
        expect(() -> Lists.last(null)).toThrow(AssertionError.class);
    }

    @Test
    public void lastShouldReturnNonPresentOptionalForEmptyList() {
        expect(Lists.last(Collections.emptyList())).not().toBe(present());
    }

    @Test
    public void lastShouldReturnLastElementForList() {
        expect(Lists.last(Arrays.asList("foo", "bar"))).toBe(optionalOf("bar"));
    }

}
