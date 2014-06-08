package org.testifj.util;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.IteratorThatIs.iteratorOf;

public class IteratorsTest {

    @Test
    public void iteratorOfShouldReturnIteratorWithMatchingContents() {
        expect(Iterators.of("foo", "bar")).toBe(iteratorOf("foo", "bar"));
    }

    @Test
    public void filteredShouldNotAcceptInvalidArguments() {
        expect(() -> Iterators.filter(null, mock(Predicate.class))).toThrow(AssertionError.class);
        expect(() -> Iterators.filter(mock(Iterator.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void filteredShouldReturnMatchingElements() {
        final Iterator<Integer> iterator = Iterators.of(1, 2, 3, 4);
        final Iterator<Integer> filtered = Iterators.filter(iterator, n -> n % 2 == 0);

        expect(filtered).toBe(iteratorOf(2, 4));
    }

    @Test
    public void collectShouldNotAcceptInvalidArguments() {
        expect(() -> Iterators.collect(null, mock(Function.class))).toThrow(AssertionError.class);
        expect(() -> Iterators.collect(mock(Iterator.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void collectShouldReturnTransformedElements() {
        final Iterator sourceIterator = Iterators.of(1, 2, 3, 4);
        final Function function = String::valueOf;
        final Iterator transformedIterator = Iterators.collect(sourceIterator, function);

        expect(transformedIterator).toBe(iteratorOf("1", "2", "3", "4"));
    }

    @Test
    public void emptyShouldNeverReturnAnyElements() {
        final Iterator<Object> iterator = Iterators.empty();

        expect(iterator.hasNext()).toBe(false);
        expect(() -> iterator.next()).toThrow(NoSuchElementException.class);
    }

}