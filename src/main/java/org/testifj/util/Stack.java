package org.testifj.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class Stack<E> implements List<E> {

    private final List<E> targetList;

    public Stack() {
        this(new ArrayList<>());
    }

    public Stack(List<E> targetList) {
        assert targetList != null : "Target list can't be null";
        this.targetList = targetList;
    }

    public void push(E element) {
        assert element != null : "Element can't be null";

        targetList.add(element);
    }

    @SuppressWarnings("unchecked")
    public E pop() {
        try {
            if (targetList instanceof Deque) {
                return ((Deque<E>) targetList).removeLast();
            }

            return targetList.remove(targetList.size() - 1);
        } catch (NoSuchElementException|ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        try {
            if (targetList instanceof Deque) {
                return ((Deque<E>) targetList).getLast();
            }

            return targetList.get(targetList.size() - 1);
        } catch (NoSuchElementException|ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }

    @Override
    public int size() {
        return targetList.size();
    }

    @Override
    public boolean isEmpty() {
        return targetList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return targetList.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return targetList.iterator();
    }

    @Override
    public Object[] toArray() {
        return targetList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return targetList.toArray(a);
    }

    public boolean add(E e) {
        return targetList.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return targetList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return targetList.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return targetList.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return targetList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return targetList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return targetList.retainAll(c);
    }

    public void replaceAll(UnaryOperator<E> operator) {
        targetList.replaceAll(operator);
    }

    public void sort(Comparator<? super E> c) {
        targetList.sort(c);
    }

    @Override
    public void clear() {
        targetList.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Stack)) {
            return false;
        }

        final Stack other = (Stack) o;

        return targetList.equals(other.targetList);
    }

    @Override
    public int hashCode() {
        return targetList.hashCode();
    }

    @Override
    public E get(int index) {
        return targetList.get(index);
    }

    public E set(int index, E element) {
        return targetList.set(index, element);
    }

    public void add(int index, E element) {
        targetList.add(index, element);
    }

    @Override
    public E remove(int index) {
        return targetList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return targetList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return targetList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return targetList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return targetList.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return targetList.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<E> spliterator() {
        return targetList.spliterator();
    }

    public boolean removeIf(Predicate<? super E> filter) {
        return targetList.removeIf(filter);
    }

    @Override
    public Stream<E> stream() {
        return targetList.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return targetList.parallelStream();
    }

    public void forEach(Consumer<? super E> action) {
        targetList.forEach(action);
    }

    @Override
    public String toString() {
        return targetList.toString();
    }
}
