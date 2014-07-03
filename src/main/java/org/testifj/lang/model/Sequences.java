package org.testifj.lang.model;

import java.util.Collections;

@SuppressWarnings("unchecked")
public final class Sequences {

    private static final Sequence EMPTY = unmodifiableSeries(new LinkedSequence());

    public static <E> Sequence<E> emptySeries() {
        return EMPTY;
    }

    public static <E> Sequence<E> sequenceOf(E... elements) {
        final Sequence<E> sequence = new LinkedSequence<>();

        Collections.addAll(sequence, elements);

        return unmodifiableSeries(sequence);
    }

    public static <E> Sequence<E> unmodifiableSeries(Sequence<E> sequence) {
        return new UnmodifiableSequence<>(sequence);
    }

}
