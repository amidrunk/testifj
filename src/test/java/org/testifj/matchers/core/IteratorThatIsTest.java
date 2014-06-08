package org.testifj.matchers.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class IteratorThatIsTest {


    @Test
    public void emptyIteratorShouldNotMatchNull() {
        expect(IteratorThatIs.emptyIterator().matches(null)).toBe(false);
    }

    @Test
    public void emptyIteratorShouldMatchIteratorOfNoElements() {
        final Iterator iterator = mock(Iterator.class);

        when(iterator.hasNext()).thenReturn(false);

        expect(IteratorThatIs.emptyIterator().matches(iterator)).toBe(true);
    }

    @Test
    public void emptyIteratorShouldNotMatchIteratorWithElements() {
        final Iterator iterator = mock(Iterator.class);

        when(iterator.hasNext()).thenReturn(true);

        expect(IteratorThatIs.emptyIterator().matches(iterator)).toBe(false);
    }

    @Test
    public void iteratorOfShouldNotMatchIteratorWithDifferentElements() {
        expect(IteratorThatIs.iteratorOf("foo", "bar").matches(Arrays.asList("baz").iterator())).toBe(false);
    }

    @Test
    public void iteratorOfShouldMatchIteratorWithEqualElements() {
        expect(IteratorThatIs.iteratorOf("foo", "bar").matches(Arrays.asList("foo", "bar").iterator())).toBe(true);
    }

    @Test
    public void iteratorOfShouldNotMatchNull() {
        expect(IteratorThatIs.iteratorOf("foo").matches(null)).toBe(false);
    }

    @Test
    public void iteratorOfSholdNotMatchIteratorWithDifferentLength() {
        expect(IteratorThatIs.iteratorOf("foo", "bar").matches(Arrays.asList("foo").iterator())).toBe(false);
        expect(IteratorThatIs.iteratorOf("foo", "bar").matches(Arrays.asList("foo", "bar", "baz").iterator())).toBe(false);
    }
}