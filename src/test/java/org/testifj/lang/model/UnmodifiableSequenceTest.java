package org.testifj.lang.model;

import org.junit.Test;

import java.util.Arrays;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;

public class UnmodifiableSequenceTest {

    private final Sequence<String> sourceSequence = new LinkedSequence<>();

    private final Sequence<String> sequence = new UnmodifiableSequence<>(sourceSequence);

    @Test
    public void constructorShouldNotAcceptNullSourceSequence() {
        expect(() -> new UnmodifiableSequence<>(null)).toThrow(AssertionError.class);
    }

    @Test
    public void firstShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo", "bar"));

        final Sequence.SingleElement<String> selector = sequence.first();

        expect(selector.exists()).toBe(true);
        expect(selector.get()).toBe("foo");

        expect(() -> selector.swap("X")).toThrow(UnsupportedOperationException.class);
        expect(() -> selector.remove()).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void lastShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo", "bar"));

        final Sequence.SingleElement<String> selector = sequence.last();

        expect(selector.exists()).toBe(true);
        expect(selector.get()).toBe("bar");

        expect(() -> selector.swap("X")).toThrow(UnsupportedOperationException.class);
        expect(() -> selector.remove()).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void atShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo"));

        final Sequence.SingleElement<String> selector = sequence.at(0);

        expect(selector.get()).toBe("foo");

        expect(() -> selector.swap("X")).toThrow(UnsupportedOperationException.class);
        expect(() -> selector.remove()).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void allShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo"));

        expect(sequence.all().get()).toBe(collectionOf("foo"));
        expect(() -> sequence.all().remove()).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void clearShouldNotBeSupported() {
        expect(() -> sequence.clear()).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void sizeShouldBeReturnedFromSourceSequence() {
        sourceSequence.add("foo");

        expect(sequence.size()).toBe(1);
    }

    @Test
    public void firstByPredicateShouldReturnUnmodifiableSelector() {
        sourceSequence.add("foo");

        final Sequence.SingleElement<String> selector = sequence.first(s -> true);

        expect(selector.exists()).toBe(true);
        expect(selector.get()).toBe("foo");
        expect(() -> selector.swap("bar")).toThrow(UnsupportedOperationException.class);
        expect(() -> selector.remove()).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void lastByPredicateShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo", "bar"));

        final Sequence.SingleElement<String> selector = sequence.last(s -> true);

        expect(selector.exists()).toBe(true);
        expect(selector.get()).toBe("bar");
        expect(() -> selector.swap("baz")).toThrow(UnsupportedOperationException.class);
        expect(() -> selector.remove()).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void insertBeforeInElementSelectorShouldBeUnsupported() {
        sourceSequence.add("foo");

        expect(() -> sequence.first().insertBefore("bar")).toThrow(UnsupportedOperationException.class);
        expect(() -> sequence.first(s -> true).insertBefore("bar")).toThrow(UnsupportedOperationException.class);
        expect(() -> sequence.last().insertBefore("bar")).toThrow(UnsupportedOperationException.class);
        expect(() -> sequence.last(s -> true).insertBefore("bar")).toThrow(UnsupportedOperationException.class);
        expect(() -> sequence.at(0).insertBefore("bar")).toThrow(UnsupportedOperationException.class);

        expect(sourceSequence).toBe(iterableOf("foo"));
    }

}