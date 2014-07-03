package org.testifj.lang.model;

import org.testifj.Predicate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Sequence<T> extends Collection<T> {

    SingleElement<T> last();

    SingleElement<T> last(Predicate<T> predicate);

    SingleElement<T> first();

    SingleElement<T> first(Predicate<T> predicate);

    /**
     * Returns a selector for the specified index. The selector will be evaluated upon every access, i.e.
     * the evaluated element can change. Example:
     * <pre>{@code
     * selector = sequence.at(0);
     * expect(selector.get()).toBe("bar");
     * selector.insertBefore("foo");
     * expect(selector.get()).toBe("bar");
     * }</pre>
     *
     * @param index The index of the element to select.
     * @return A selector for the element at the specified index.
     */
    SingleElement<T> at(int index);

    MultipleElements<T> all();

    void clear();

    boolean isEmpty();

    int size();

    interface ElementSelector<T> {

        void remove();

    }

    interface SingleElement<T> extends ElementSelector<T> {

        void swap(T newElement);

        boolean exists();

        T get();

        /**
         * Inserts an element before the element that matches this selector. This can cause the selector to
         * evaluate to a different element, causing the {@link org.testifj.lang.model.Sequence.SingleElement#get()}
         * method to return the new element. For example:
         * <pre>{@code
         * selector = sequence.first();
         * expect(selector.get()).toBe("bar");
         * selector.insertBefore("foo");
         * expect(selector.get()).toBe("foo");
         * }</pre>
         *
         * @param element The element that should be inserted.
         */
        void insertBefore(T element);

        // TODO insertAfter

        default Optional<T> optional() {
            return (exists() ? Optional.of(get()) : Optional.empty());
        }

    }

    interface MultipleElements<T> extends ElementSelector<T> {

        List<T> get();

    }

}
