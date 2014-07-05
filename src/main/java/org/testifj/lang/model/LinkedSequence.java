package org.testifj.lang.model;

import org.testifj.Predicate;
import org.testifj.util.SuppliedIterator;

import java.util.*;
import java.util.function.Supplier;

public final class LinkedSequence<T> extends AbstractCollection<T> implements Sequence<T> {

    private Link<T> first;

    private Link<T> last;

    private int size;

    private int version = 0;

    @Override
    public boolean add(T element) {
        assert element != null : "Element can't be null";

        final Link<T> newLink = new Link<>(element);

        if (first == null) {
            first = last = newLink;
        } else {
            last.next(newLink);
            newLink.previous(last);
            last = newLink;
        }

        size++;
        version++;

        return true;
    }

    @Override
    public SingleElement<T> last() {
        return new LinkSelector(last);
    }

    @Override
    public SingleElement<T> last(Predicate<T> predicate) {
        assert predicate != null : "Predicate can't be null";

        Link<T> current = last;

        while (current != null) {
            if (predicate.test(current.element())) {
                return new LinkSelector(current);
            }

            current = current.previous();
        }

        return new LinkSelector(null);
    }

    @Override
    public SingleElement<T> first() {
        return new LinkSelector(first);
    }

    @Override
    public SingleElement<T> first(Predicate<T> predicate) {
        assert predicate != null : "Predicate can't be null";

        Link<T> current = first;

        while (current != null) {
            if (predicate.test(current.element())) {
                return new LinkSelector(current);
            }

            current = current.next();
        }

        return new LinkSelector(null);
    }

    @Override
    public SingleElement<T> at(int index) {
        assert index >= 0 : "Index must be positive";

        Link<T> current = first;

        while (index-- > 0 && current != null) {
            current = current.next();
        }

        return new LinkSelector(current);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultipleElements<T> all() {
        final Link<T>[] links = new Link[size];

        Link<T> current = first;

        for (int i = 0; i < size; i++) {
            links[i] = current;

            current = current.next;
        }

        final int snapshotVersion = version;

        return new MultipleElements<T>() {
            @Override
            public List<T> get() {
                final ArrayList<T> copy = new ArrayList<>();

                for (Link<T> link :links) {
                    copy.add(link.element());
                }

                return copy;
            }

            @Override
            public void remove() {
                if (version == snapshotVersion) {
                    clear();
                } else {
                    for (Link link : links) {
                        removeLink(link);
                    }
                }
            }
        };
    }

    @Override
    public void clear() {
        first = last = null;
        size = 0;
        version++;
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new SuppliedIterator<>(new Supplier<Optional<T>>() {

            private final int version = LinkedSequence.this.version;

            private Link<T> current = first;

            @Override
            public Optional<T> get() {
                if (current == null) {
                    return Optional.empty();
                }

                if (LinkedSequence.this.version != version) {
                    throw new ConcurrentModificationException();
                }

                final T element = current.element;
                current = current.next;
                return Optional.of(element);
            }
        });
    }

    private static final class Link<T> {

        private T element;

        private Link<T> previous;

        private Link<T> next;

        private Link(T element) {
            this.element = element;
        }

        T element() {
            return element;
        }

        void element(T element) {
            assert element != null : "Element can't be null";
            this.element = element;
        }

        Link<T> previous() {
            return previous;
        }

        void previous(Link<T> previous) {
            this.previous = previous;
        }

        Link<T> next() {
            return next;
        }

        void next(Link<T> next) {
            this.next = next;
        }

    }

    private class LinkSelector implements SingleElement<T> {

        private Link<T> link;

        private int snapshotVersion;

        private LinkSelector(Link<T> link) {
            this.link = link;
            this.snapshotVersion = version;
        }

        @Override
        public void swap(T newElement) {
            assert newElement != null : "Element can't be null";

            checkExists();
            checkVersion();

            link.element(newElement);

            upgrade();
        }

        @Override
        public boolean exists() {
            return (link != null);
        }

        @Override
        public T get() {
            checkExists();
            checkVersion();

            return link.element();
        }

        @Override
        public void insertBefore(T element) {
            assert element != null : "Element can't be null";

            checkExists();
            checkVersion();

            final Link<T> newLink = new Link<>(element);

            newLink.previous = link.previous;
            newLink.next = link;

            if (link.previous == null) {
                first = newLink;
            } else {
                link.previous.next = newLink;
            }

            link.previous = newLink;

            size++;

            upgrade();
        }

        @Override
        public void remove() {
            checkExists();
            checkVersion();

            removeLink(link);

            snapshotVersion = version;
        }

        private void upgrade() {
            snapshotVersion = ++version;
        }

        private void checkExists() {
            if (!exists()) {
                throw new NoSuchElementException();
            }
        }

        private void checkVersion() {
            if (version != snapshotVersion) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private void removeLink(Link<T> link) {
        if (link.previous == null) {
            first = link.next;
        } else {
            link.previous.next = link.next;
        }

        if (link.next == null) {
            last = link.previous;
        } else {
            link.next.previous = link.previous;
        }

        size--;
        version++;
    }

}
