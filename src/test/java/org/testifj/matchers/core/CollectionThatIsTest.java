package org.testifj.matchers.core;

import org.junit.Test;
import org.testifj.Matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.CollectionThatIs.collectionWithElements;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

@SuppressWarnings("unchecked")
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
        expect(collectionOf("foo", "bar").matches(Arrays.asList("foo"))).toBe(false);
        expect(collectionOf("foo", "bar").matches(Arrays.asList("foo", "baz"))).toBe(false);
        expect(collectionOf("foo", "bar").matches(Arrays.asList("foo", "bar", "baz"))).toBe(false);
    }

    @Test
    public void collectionOfShouldMatchCollectionWithEqualElements() {
        expect(collectionOf("foo", "bar").matches(Arrays.asList("foo", "bar"))).toBe(true);
    }

    @Test
    public void collectionOfWithEmptyArrayShouldMatchEmptyCollection() {
        expect(CollectionThatIs.<Object, Collection<Object>>collectionWithElements().matches(Collections.emptyList())).toBe(true);
    }

    @Test
    public void collectionOfWithMatchersShouldNotAcceptNullMatchers() {
        expect(() -> collectionWithElements((Matcher[]) null)).toThrow(AssertionError.class);
    }

    @Test
    public void collectionWithElementsShouldNotMatchNull() {
        expect(collectionWithElements().matches(null)).toBe(false);
    }

    @Test
    public void collectionOfWithZeroMatchersShouldMatchEmptyCollectionOnly() {
        given(collectionWithElements()).then(matcher -> {
            expect(matcher.matches(Collections.emptyList())).toBe(true);
            expect(matcher.matches(Arrays.asList("foo"))).toBe(false);
        });
    }

    @Test
    public void collectionOfWithMatchersShouldNotMatchIfSizeIsIncorrect() {
        final Matcher matcher = mock(Matcher.class);

        when(matcher.matches(any())).thenReturn(true);

        expect(collectionWithElements(matcher).matches(Arrays.asList("foo", "bar"))).toBe(false);
    }

    @Test
    public void collectionOfWithMatchersShouldNotMatchIfAnyMatcherFails() {
        expect(collectionWithElements(equalTo("foo"), equalTo("bar")).matches(Arrays.asList("foo", "baz"))).toBe(false);
    }

    @Test
    public void collectionOfWithMatchersShouldMatchIfAllMatchersMatch() {
        expect(collectionWithElements(equalTo("foo"), equalTo("bar")).matches(Arrays.asList("foo", "bar"))).toBe(true);
    }

}
