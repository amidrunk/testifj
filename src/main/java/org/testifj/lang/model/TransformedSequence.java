package org.testifj.lang.model;

import org.testifj.Predicate;
import org.testifj.util.Iterators;
import org.testifj.util.Lists;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public final class TransformedSequence<T, S> extends AbstractCollection<T> implements Sequence<T> {

    private final Sequence<S> sourceSequence;

    private final Function<S, T> sourceToTarget;

    private final Function<T, S> targetToSource;

    public TransformedSequence(Sequence<S> sourceSequence, Function<S, T> sourceToTarget, Function<T, S> targetToSource) {
        this.sourceSequence = sourceSequence;
        this.sourceToTarget = sourceToTarget;
        this.targetToSource = targetToSource;
    }

    @Override
    public boolean add(T element) {
        assert element != null : "Element can't be null";

        return sourceSequence.add(targetToSource.apply(element));
    }

    @Override
    public SingleElement<T> last() {
        return transformed(sourceSequence.last());
    }

    @Override
    public SingleElement<T> last(Predicate<T> predicate) {
        assert predicate != null : "Predicate can't be null";
        return transformed(sourceSequence.last(s -> predicate.test(sourceToTarget.apply(s))));
    }

    @Override
    public SingleElement<T> first(Predicate<T> predicate) {
        assert predicate != null : "Predicate can't be null";
        return transformed(sourceSequence.first(instance -> predicate.test(sourceToTarget.apply(instance))));
    }

    @Override
    public SingleElement<T> first() {
        return transformed(sourceSequence.first());
    }

    @Override
    public SingleElement<T> at(int index) {
        return transformed(sourceSequence.at(index));
    }

    @Override
    public MultipleElements<T> all() {
        return transformed(sourceSequence.all());
    }

    @Override
    public MultipleElements<T> tail(int offset) {
        return transformed(sourceSequence.tail(offset));
    }

    @Override
    public void clear() {
        sourceSequence.clear();
    }

    @Override
    public boolean isEmpty() {
        return sourceSequence.isEmpty();
    }

    @Override
    public int size() {
        return sourceSequence.size();
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.collect(sourceSequence.iterator(), sourceToTarget);
    }

    private MultipleElements<T> transformed(final MultipleElements<S> selector) {
        return new MultipleElements<T>() {

            @Override
            public boolean exists() {
                return selector.exists();
            }

            @Override
            public List<T> get() {
                return Lists.collect(selector.get(), sourceToTarget);
            }

            @Override
            public void remove() {
                selector.remove();
            }
        };
    }

    private SingleElement<T> transformed(final SingleElement<S> selector) {
        return new TransformedSingleElement(selector);
    }

    private final class TransformedSingleElement implements SingleElement<T> {

        private final SingleElement<S> selector;

        private TransformedSingleElement(SingleElement<S> selector) {
            this.selector = selector;
        }

        @Override
        public void swap(T newElement) {
            selector.swap(targetToSource.apply(newElement));
        }

        @Override
        public boolean exists() {
            return selector.exists();
        }

        @Override
        public T get() {
            return sourceToTarget.apply(selector.get());
        }

        @Override
        public SingleElement<T> previous() {
            return new TransformedSingleElement(selector.previous());
        }

        @Override
        public void remove() {
            selector.remove();
        }

        @Override
        public void insertBefore(T element) {
            selector.insertBefore(targetToSource.apply(element));
        }

    }
}
