package org.testifj.lang.model;

import org.testifj.Predicate;
import org.testifj.util.SuppliedIterator;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class UnmodifiableSequence<E> extends AbstractCollection<E> implements Sequence<E> {

    private final Sequence<E> sourceSequence;

    public UnmodifiableSequence(Sequence<E> sourceSequence) {
        assert sourceSequence != null : "Source sequence can't be null";
        this.sourceSequence = sourceSequence;
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<E> sourceIterator = sourceSequence.iterator();

        return new SuppliedIterator<>(() -> {
            if (!sourceIterator.hasNext()) {
                return Optional.empty();
            }

            return Optional.of(sourceIterator.next());
        });
    }

    @Override
    public int size() {
        return sourceSequence.size();
    }

    @Override
    public boolean add(E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SingleElement<E> last() {
        return unmodifiableSelector(sourceSequence.last());
    }

    @Override
    public SingleElement<E> first() {
        return unmodifiableSelector(sourceSequence.first());
    }

    @Override
    public SingleElement<E> first(Predicate<E> predicate) {
        return unmodifiableSelector(sourceSequence.first(predicate));
    }

    @Override
    public SingleElement<E> at(int index) {
        return unmodifiableSelector(sourceSequence.at(index));
    }

    @Override
    public MultipleElements<E> all() {
        return unmodifiableSelector(sourceSequence.all());
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SingleElement<E> last(Predicate<E> predicate) {
        return unmodifiableSelector(sourceSequence.last(predicate));
    }

    private MultipleElements<E> unmodifiableSelector(final MultipleElements<E> selector) {
        return new MultipleElements<E>() {
            @Override
            public List<E> get() {
                return selector.get();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private SingleElement<E> unmodifiableSelector(final SingleElement<E> selector) {
        return new SingleElement<E>() {
            @Override
            public void swap(E newElement) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void insertBefore(E element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean exists() {
                return selector.exists();
            }

            @Override
            public E get() {
                return selector.get();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
