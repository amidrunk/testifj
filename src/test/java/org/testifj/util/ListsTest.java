package org.testifj.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;
import static org.testifj.matchers.core.OptionalThatIs.present;

@SuppressWarnings("unchecked")
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

    @Test
    public void optionallyCollectShouldNotAcceptInvalidArguments() {
        expect(() -> Lists.optionallyCollect(null, mock(Function.class))).toThrow(AssertionError.class);
        expect(() -> Lists.optionallyCollect(mock(List.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void optionallyCollectShouldReturnEmptyOptionalForSameElements() {
        expect(Lists.optionallyCollect(Arrays.asList("foo", "bar", "baz"), str -> str)).not().toBe(present());
    }

    @Test
    public void optionallyCollectShouldReturnNewListIfAnyElementIsTransformed() {
        final Function<String, String> function = mock(Function.class);

        when(function.apply(eq("foo"))).thenAnswer(returnsFirstArg());
        when(function.apply(eq("bar"))).thenReturn("BAR");
        when(function.apply(eq("baz"))).thenAnswer(returnsFirstArg());

        expect(Lists.optionallyCollect(Arrays.asList("foo", "bar", "baz"), function)).toBe(optionalOf(Arrays.asList("foo", "BAR", "baz")));
    }

    @Test
    public void zipShouldNotAcceptInvalidArguments() {
        expect(() -> Lists.zip(null, Collections.emptyList())).toThrow(AssertionError.class);
        expect(() -> Lists.zip(Collections.emptyList(), null)).toThrow(AssertionError.class);
    }

    @Test
    public void zipShouldNotAcceptListsOfDifferentLengths() {
        expect(() -> Lists.<String, Integer>zip(Arrays.asList("foo"), Arrays.asList(1, 2, 3))).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void zipShouldReturnListOfPairedElements() {
        expect(Lists.zip(Arrays.asList("a", "b", "c"), Arrays.asList(1, 2, 3))).toBe(collectionOf(
                new Pair<>("a", 1), new Pair<>("b", 2), new Pair<>("c", 3)
        ));
    }

    @Test
    public void collectShouldNotAcceptInvalidArguments() {
        expect(() -> Lists.collect(null, mock(Function.class))).toThrow(AssertionError.class);
        expect(() -> Lists.collect(mock(List.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void collectShouldReturnNewElements() {
        expect(Lists.collect(Arrays.asList("a", "ab", "abc"), String::length)).toBe(collectionOf(1, 2, 3));
    }

}
