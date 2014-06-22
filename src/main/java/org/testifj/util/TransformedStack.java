package org.testifj.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class TransformedStack<S, T> implements Stack<S> {

    private final Stack<T> targetStack;

    private final Function<S, T> sourceToTarget;

    private final Function<T, S> targetToSource;

    private final List<StackListenerDelegate> stackListeners = new LinkedList<>();

    public TransformedStack(Stack<T> targetStack, Function<S, T> sourceToTarget, Function<T, S> targetToSource) {
        assert targetStack != null : "Target stack can't be null";
        assert sourceToTarget != null : "Source to target converter can't be null";
        assert targetToSource != null : "Target source source converter can't be null";

        this.targetStack = targetStack;
        this.sourceToTarget = sourceToTarget;
        this.targetToSource = targetToSource;
    }

    @Override
    public void addStackListener(StackListener<S> stackListener) {
        assert stackListener != null : "Stack listener can't be null";

        final StackListenerDelegate delegate = new StackListenerDelegate(stackListener);
        targetStack.addStackListener(delegate);
        stackListeners.add(delegate);
    }

    @Override
    public void removeStackListener(StackListener<S> stackListener) {
        assert stackListener != null : "Stack listener can't be null";

        for (Iterator<StackListenerDelegate> iterator = stackListeners.iterator(); iterator.hasNext(); ) {
            final StackListenerDelegate delegate = iterator.next();

            if (Objects.equals(delegate.targetStackListener, stackListener)) {
                targetStack.removeStackListener(delegate);
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void push(S element) {
        targetStack.push(sourceToTarget.apply(element));
    }

    @Override
    public S pop() {
        return targetToSource.apply(targetStack.pop());
    }

    @Override
    public S peek() {
        return targetToSource.apply(targetStack.peek());
    }

    @Override
    public void insert(int index, S element) {
        targetStack.insert(index, sourceToTarget.apply(element));
    }

    @Override
    public int size() {
        return targetStack.size();
    }

    @Override
    public boolean isEmpty() {
        return targetStack.isEmpty();
    }

    @Override
    public List<S> tail(int fromIndex) {
        final List<T> targetList = targetStack.tail(fromIndex);

        return new AbstractList<S>() {
            @Override
            public S get(int index) {
                return targetToSource.apply(targetList.get(index));
            }

            @Override
            public int size() {
                return targetList.size();
            }
        };
    }

    @Override
    public Stream<S> stream() {
        return targetStack.stream().map(targetToSource);
    }

    @Override
    public void clear() {
        targetStack.clear();
    }

    @Override
    public S swap(S newElement) {
        return targetToSource.apply(targetStack.swap(sourceToTarget.apply(newElement)));
    }

    @Override
    public Iterator<S> iterator() {
        return Iterators.collect(targetStack.iterator(), targetToSource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransformedStack that = (TransformedStack) o;

        if (!sourceToTarget.equals(that.sourceToTarget)) return false;
        if (!targetStack.equals(that.targetStack)) return false;
        if (!targetToSource.equals(that.targetToSource)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetStack.hashCode();
        result = 31 * result + sourceToTarget.hashCode();
        result = 31 * result + targetToSource.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TransformedStack{" +
                "targetStack=" + targetStack +
                ", sourceToTarget=" + sourceToTarget +
                ", targetToSource=" + targetToSource +
                '}';
    }

    private class StackListenerDelegate implements StackListener<T> {

        private final StackListener<S> targetStackListener;

        private StackListenerDelegate(StackListener<S> targetStackListener) {
            this.targetStackListener = targetStackListener;
        }

        @Override
        public void onElementPushed(Stack<T> stack, T element) {
            targetStackListener.onElementPushed(TransformedStack.this, targetToSource.apply(element));
        }

        @Override
        public void onElementPopped(Stack<T> stack, T element) {
            targetStackListener.onElementPopped(TransformedStack.this, targetToSource.apply(element));
        }

        @Override
        public void onElementInserted(Stack<T> stack, T element, int index) {
            targetStackListener.onElementInserted(TransformedStack.this, targetToSource.apply(element), index);
        }

        @Override
        public void onElementSwapped(Stack<T> stack, T oldElement, T newElement) {
            targetStackListener.onElementSwapped(TransformedStack.this, targetToSource.apply(oldElement), targetToSource.apply(newElement));
        }
    }
}
