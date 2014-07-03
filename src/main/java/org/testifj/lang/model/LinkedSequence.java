package org.testifj.lang.model;

import org.testifj.Predicate;
import org.testifj.util.SuppliedIterator;

import java.util.*;
import java.util.function.Supplier;

public final class LinkedSequence<T> extends AbstractCollection<T> implements Sequence<T> {

    private final MultipleElements<T> ALL = new MultipleElements<T>() {
        @Override
        public List<T> get() {
            final ArrayList<T> list = new ArrayList<>(size);

            Link<T> link = first;

            while (link != null) {
                list.add(link.element());

                link = link.next;
            }

            return list;
        }

        @Override
        public void remove() {
            clear();
        }
    };

    private final SingleElement<T> FIRST = new SingleElement<T>() {
        @Override
        public void swap(T newElement) {
            checkNotEmpty();
            first.element(newElement);
            version++;
        }

        @Override
        public boolean exists() {
            return first != null;
        }

        @Override
        public T get() {
            checkNotEmpty();
            return first.element();
        }

        @Override
        public void insertBefore(T element) {
            assert element != null : "Element can't be null";

            checkNotEmpty();

            final Link<T> newLink = new Link<>(element);

            newLink.next = first;
            first.previous = newLink;
            first = newLink;

            size++;
            version++;
        }

        @Override
        public void remove() {
            checkNotEmpty();

            if (first.next == null) {
                first = last = null;
            } else {
                first.next.previous(null);
                first = first.next;
            }

            size--;
            version++;
        }
    };

    private final SingleElement<T> LAST = new SingleElement<T>() {
        @Override
        public void swap(T newElement) {
            checkNotEmpty();
            last.element(newElement);
            version++;
        }

        @Override
        public void insertBefore(T element) {
            assert element != null : "Element can't be null";

            checkNotEmpty();

            final Link<T> newLink = new Link<>(element);

            newLink.next = last;

            if (last.previous == null) {
                first = newLink;
            } else {
                last.previous.next = newLink;
            }

            last.previous = newLink;

            size++;
            version++;
        }

        @Override
        public boolean exists() {
            return last != null;
        }

        @Override
        public T get() {
            checkNotEmpty();
            return last.element;
        }

        @Override
        public void remove() {
            checkNotEmpty();

            if (last.previous == null) {
                last = first = null;
            } else {
                last.previous.next = null;
                last = last.previous;
            }

            size--;
            version++;
        }
    };

    private void insertBefore(Link<T> anchor, Link<T> newLink) {
        newLink.next = anchor;
        newLink.previous = anchor.previous;

        if (anchor.previous == null) {
            first = newLink;
        } else {
            anchor.previous.next = newLink;
        }

        anchor.previous = newLink;

        size++;
        version++;
    }

    private final class StatementAtIndex implements SingleElement<T> {

        private final int index;

        private StatementAtIndex(int index) {
            this.index = index;
        }

        @Override
        public void insertBefore(T element) {
            assert element != null : "Element can't be null";

            if (index == size - 1) {
                LAST.insertBefore(element);
            } else {
                LinkedSequence.this.insertBefore(link(), new Link<T>(element));
            }
        }

        @Override
        public void swap(T newElement) {
            assert newElement != null : "Element can't be null";

            if (index == size - 1) {
                LAST.swap(newElement);
            } else {
                link().element(newElement);
            }

            version++;
        }

        @Override
        public boolean exists() {
            return index < size;
        }

        @Override
        public T get() {
            if (index >= size) {
                throw new NoSuchElementException("Index must be < " + size);
            }

            return link().element();
        }

        @Override
        public void remove() {
            if (index == size - 1) {
                LAST.remove();
            } else {
                final Link link = link();

                link.previous.next = link.next;
                link.next.previous = link.previous;

                size--;
                version++;
            }
        }

        private Link<T> link() {
            Link<T> current = first;

            for (int i = 0; i < index; i++) {
                current = current.next();
            }

            return current;
        }
    }

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
        return LAST;
    }

    @Override
    public SingleElement<T> last(Predicate<T> predicate) {
        assert predicate != null : "Predicate can't be null";

        final Supplier<Optional<Link<T>>> supplier = () -> {
            Link<T> current = last;

            while (current != null) {
                if (predicate.test(current.element())) {
                    return Optional.of(current);
                }

                current = current.previous();
            }

            return Optional.empty();
        };

        return suppliedSelector(supplier);
    }

    @Override
    public SingleElement<T> first() {
        return FIRST;
    }

    @Override
    public SingleElement<T> first(Predicate<T> predicate) {
        assert predicate != null : "Predicate can't be null";

        final Supplier<Optional<Link<T>>> supplier = () -> {
            Link<T> current = first;

            while (current != null) {
                if (predicate.test(current.element())) {
                    return Optional.of(current);
                }

                current = current.next();
            }

            return Optional.empty();
        };

        return suppliedSelector(supplier);
    }

    @Override
    public SingleElement<T> at(int index) {
        assert index >= 0 : "Index must be positive";

        if (index == 0) {
            return FIRST;
        }

        return new StatementAtIndex(index);
    }

    @Override
    public MultipleElements<T> all() {
        return ALL;
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

    private SingleElement<T> suppliedSelector(final Supplier<Optional<Link<T>>> supplier) {
        return new SingleElement<T>() {

            private int expectedVersion = -1;

            private Optional<Link<T>> link = null;

            @Override
            public void insertBefore(T element) {
                assert element != null;

                final Optional<Link<T>> optional = link();

                if (!optional.isPresent()) {
                    throw new NoSuchElementException();
                }

                final Link<T> link = optional.get();

                if (link == first) {
                    FIRST.insertBefore(element);
                } else if (link == last) {
                    LAST.insertBefore(element);
                } else {
                    LinkedSequence.this.insertBefore(link, new Link<T>(element));
                    this.link = null;
                }
            }

            @Override
            public void swap(T newElement) {
                assert newElement != null : "Element can't be null";

                final Optional<Link<T>> optional = link();

                if (!optional.isPresent()) {
                    throw new NoSuchElementException();
                }

                optional.get().element(newElement);
            }

            @Override
            public boolean exists() {
                return link().isPresent();
            }

            @Override
            public T get() {
                return link().get().element();
            }

            @Override
            public void remove() {
                final Optional<Link<T>> optional = link();

                if (!optional.isPresent()) {
                    throw new NoSuchElementException();
                }

                final Link<T> link = optional.get();

                if (link == first) {
                    FIRST.remove();
                } else if (link == last) {
                    LAST.remove();
                } else {
                    link.previous.next = link.next;
                    link.next.previous = link.previous;
                    size--;
                    version++;
                }
            }

            private Optional<Link<T>> link() {
                if (link == null) {
                    this.expectedVersion = version;
                    this.link = supplier.get();
                } else {
                    if (expectedVersion != version) {
                        throw new ConcurrentModificationException();
                    }
                }

                return this.link;
            }
        };
    }

    private void checkNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Empty sequence (size=" + size + ", first=" + first + ", last=" + last + ")");
        }
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
}
