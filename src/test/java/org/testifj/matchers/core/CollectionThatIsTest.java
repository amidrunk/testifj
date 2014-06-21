package org.testifj.matchers.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testifj.Expect.expect;

public class CollectionThatIsTest {

    @Test
    public void emptyShouldMatchEmptyCollection() {
        expect(CollectionThatIs.empty().matches(Collections.emptyList())).toBe(true);
    }

    @Test
    public void emptyShouldNotMatchNonEmptyCollection() {
        expect(CollectionThatIs.empty().matches(Arrays.asList("foo"))).toBe(false);
    }

    @Test
    public void emptyShouldNotMatchNull() {
        expect(CollectionThatIs.empty().matches(null)).toBe(false);
    }

    @Test
    public void collectionOfShouldNotAcceptNullArray() {
        expect(() -> CollectionThatIs.<Object, List<Object>>collectionOf((Object[])null)).toThrow(AssertionError.class);
    }

    @Test
    public void collectionOfShouldNotMatchCollectionWithDifferentElements() {
        expect(CollectionThatIs.collectionOf("foo", "bar").matches(Arrays.asList("foo"))).toBe(false);
        expect(CollectionThatIs.collectionOf("foo", "bar").matches(Arrays.asList("foo", "baz"))).toBe(false);
        expect(CollectionThatIs.collectionOf("foo", "bar").matches(Arrays.asList("foo", "bar", "baz"))).toBe(false);
    }

    @Test
    public void collectionOfShouldMatchCollectionWithEqualElements() {
        expect(CollectionThatIs.collectionOf("foo", "bar").matches(Arrays.asList("foo", "bar"))).toBe(true);
    }

    @Test
    public void collectionOfWithEmptyArrayShouldMatchEmptyCollection() {
        expect(CollectionThatIs.collectionOf().matches(Collections.emptyList())).toBe(true);
    }

}
