package org.testifj.lang.model;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.lang.model.Sequences.sequenceOf;
import static org.testifj.lang.model.Sequences.unmodifiableSeries;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;

public class SequencesTest {

    @Test
    public void emptySeriesShouldReturnEmptyImmutableSeries() {
        final Sequence<Object> sequence = Sequences.emptySeries();

        expect(sequence.isEmpty()).toBe(true);
        expect(sequence.size()).toBe(0);

        expect(() -> sequence.add("foo")).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void unmodifiableSeriesShouldNotAcceptNullSource() {
        expect(() -> unmodifiableSeries(null)).toThrow(AssertionError.class);
    }

    @Test
    public void unmodifiableSeriesShouldReturnReadOnlyViewOfSeries() {
        final Sequence<String> sequence = new LinkedSequence<>();

        sequence.add("foo");
        sequence.add("bar");

        final Sequence<String> unmodifiableSequence = unmodifiableSeries(sequence);

        expect(unmodifiableSequence).toBe(iterableOf("foo", "bar"));
        expect(() -> unmodifiableSequence.add("baz")).toThrow(UnsupportedOperationException.class);
        expect(() -> unmodifiableSequence.remove("foo")).toThrow(UnsupportedOperationException.class);
        expect(() -> unmodifiableSequence.clear()).toThrow(UnsupportedOperationException.class);
        expect(() -> unmodifiableSequence.all().remove()).toThrow(UnsupportedOperationException.class);
        expect(() -> unmodifiableSequence.first().swap("baz")).toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void sequenceOfShouldReturnUnmodifiableSequenceWithElements() {
        final Sequence<String> sequence = sequenceOf("x", "y", "z");

        expect(sequence).toBe(iterableOf("x", "y", "z"));
        expect(() -> sequence.add("foo")).toThrow(UnsupportedOperationException.class);
    }

}