package org.testifj.util;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.function.Function;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ArrayThatIs.arrayOf;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.IteratorThatIs.iteratorOf;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

@SuppressWarnings("unchecked")
public class TransformedStackTest {

    private final Stack<Integer> targetStack = mock(Stack.class);

    private final Function<String, Integer> sourceToTarget = Integer::parseInt;
    private final Function<Integer, String> targetToSource = Object::toString;
    private final Stack<String> stack = new TransformedStack<String, Integer>(targetStack, sourceToTarget, targetToSource);
    private final StackListener stackListener = mock(StackListener.class);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new TransformedStack<String, Integer>(null, sourceToTarget, targetToSource)).toThrow(AssertionError.class);
        expect(() -> new TransformedStack<String, Integer>(targetStack, null, targetToSource)).toThrow(AssertionError.class);
        expect(() -> new TransformedStack<String, Integer>(targetStack, sourceToTarget, null)).toThrow(AssertionError.class);
    }

    @Test
    public void pushShouldPushTransformedElementToTargetStack() {
        stack.push("1234");
        verify(targetStack).push(eq(1234));
    }

    @Test
    public void peekShouldReturnTransformedElementFromTargetStack() {
        when(targetStack.peek()).thenReturn(1234);
        expect(stack.peek()).toBe("1234");
        verify(targetStack).peek();
    }

    @Test
    public void popShouldReturnTransformedElementFromTargetStack() {
        when(targetStack.pop()).thenReturn(1234);
        expect(stack.pop()).toBe("1234");
        verify(targetStack).pop();
    }

    @Test
    public void sizeShouldBeReturnedFromTargetStack() {
        when(targetStack.size()).thenReturn(100);
        expect(stack.size()).toBe(100);
        verify(targetStack).size();
    }

    @Test
    public void insertShouldInsertTransformedElementInTargetStack() {
        stack.insert(1234, "1000");
        verify(targetStack).insert(eq(1234), eq(1000));
    }

    @Test
    public void isEmptyShouldBeReturnedFromTargetStack() {
        when(targetStack.isEmpty()).thenReturn(true);
        expect(stack.isEmpty()).toBe(true);
        verify(targetStack).isEmpty();
    }

    @Test
    public void tailShouldReturnTransformedTail() {
        when(targetStack.tail(eq(2))).thenReturn(Arrays.asList(1, 2, 3, 4));
        expect(stack.tail(2)).toBe(collectionOf("1", "2", "3", "4"));
        verify(targetStack).tail(eq(2));
    }

    @Test
    public void streamShouldReturnTransformedStreamFromTargetStack() {
        when(targetStack.stream()).thenReturn(Arrays.asList(1, 2, 3).stream());
        final String[] result = stack.stream().toArray(String[]::new);

        expect(result).toBe(arrayOf("1", "2", "3"));

        verify(targetStack).stream();
    }

    @Test
    public void clearShouldClearTargetStack() {
        stack.clear();
        verify(targetStack).clear();
    }

    @Test
    public void iteratorShouldReturnTransformedIterator() {
        when(targetStack.iterator()).thenReturn(Arrays.asList(1, 2, 3).iterator());

        expect(stack.iterator()).toBe(iteratorOf("1", "2", "3"));

        verify(targetStack).iterator();
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(stack).toBe(equalTo(stack));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(stack).not().toBe(equalTo(null));
        expect((Object) stack).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualTargetStacksAndTransformersShouldBeEqual() {
        final TransformedStack<String, Integer> other = new TransformedStack<>(targetStack, sourceToTarget, targetToSource);

        expect(stack).toBe(equalTo(other));
        expect(stack.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        expect(stack.toString()).to(containString(targetStack.toString()));
        expect(stack.toString()).to(containString(sourceToTarget.toString()));
        expect(stack.toString()).to(containString(targetToSource.toString()));
    }

    @Test
    public void addStackListenerShouldNotAcceptNullArg() {
        expect(() -> stack.addStackListener(null)).toThrow(AssertionError.class);
    }

    @Test
    public void addStackListenerShouldAddStackListenerOnTarget() {
        stack.addStackListener(stackListener);
        verify(targetStack).addStackListener(any());
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsPushed() {
        stack.addStackListener(stackListener);

        targetListener().onElementPushed(targetStack, 1234);

        verify(stackListener).onElementPushed(eq(stack), eq("1234"));
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsPopped() {
        stack.addStackListener(stackListener);

        targetListener().onElementPopped(targetStack, 1234);

        verify(stackListener).onElementPopped(eq(stack), eq("1234"));
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsInserted() {
        stack.addStackListener(stackListener);

        targetListener().onElementInserted(targetStack, 1234, 100);

        verify(stackListener).onElementInserted(eq(stack), eq("1234"), eq(100));
    }

    @Test
    public void removeStackListenerShouldNotAcceptNullArg() {
        expect(() -> stack.removeStackListener(null)).toThrow(AssertionError.class);
    }

    @Test
    public void removeStackListenerShouldRemoveListenerFromStack() {
        stack.addStackListener(stackListener);

        final StackListener targetListener = targetListener();

        stack.removeStackListener(stackListener);

        verify(targetStack).removeStackListener(targetListener);
    }

    @Test
    public void swapShouldSwapOnTargetAndReturnResult() {
        when(targetStack.swap(1)).thenReturn(2);

        expect(stack.swap("1")).toBe("2");
    }

    @Test
    public void listenerShouldGetNotifiedWhenStackElementIsSwapped() {
        stack.addStackListener(stackListener);

        targetListener().onElementSwapped(targetStack, 1, 2);

        verify(stackListener).onElementSwapped(eq(stack), eq("1"), eq("2"));
    }

    private StackListener targetListener() {
        final ArgumentCaptor<StackListener> captor = ArgumentCaptor.forClass(StackListener.class);
        verify(targetStack).addStackListener(captor.capture());
        return captor.getValue();
    }

}