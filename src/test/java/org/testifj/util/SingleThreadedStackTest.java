package org.testifj.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InOrder;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.function.Supplier;

import static org.junit.runners.Parameterized.Parameters;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ArrayThatIs.arrayOf;
import static org.testifj.matchers.core.IterableThatIs.emptyIterable;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

@SuppressWarnings("unchecked")
@RunWith(Parameterized.class)
public class SingleThreadedStackTest {

    private final StackListener stackListener = mock(StackListener.class);

    @Parameters
    public static Iterable<Object[]> stacks() {
        return Arrays.<Object[]>asList(
            new Object[] { (Supplier) SingleThreadedStack::new},
            new Object[] { (Supplier) () -> new SingleThreadedStack<>(new LinkedList<>()) }
        );
    }

    private final Stack<String> stack;

    public SingleThreadedStackTest(Supplier<Stack<String>> stackSupplier) {
        this.stack = stackSupplier.get();
    }

    @Test
    public void constructorShouldNotAcceptNullBackingList() {
        expect(() -> new SingleThreadedStack<String>(null)).toThrow(AssertionError.class);
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
    public void instancesWithEqualContentsShouldBeEqual() {
        final SingleThreadedStack<String> other = new SingleThreadedStack<>();

        stack.push("foo");
        other.push("foo");

        expect(stack).toBe(equalTo(other));
        expect(stack.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringShouldReturnStringWithContents() {
        stack.push("foo");
        stack.push("bar");

        expect(stack.toString()).to(containString("foo"));
        expect(stack.toString()).to(containString("bar"));
    }

    @Test
    public void pushShouldNotAcceptNullElement() {
        expect(() -> stack.push(null)).toThrow(AssertionError.class);
    }

    @Test
    public void popShouldFailIfStackIsEmpty() {
        expect(() -> stack.pop()).toThrow(EmptyStackException.class);
    }

    @Test
    public void peekShouldFailIfStackIsEmpty() {
        expect(() -> stack.peek()).toThrow(EmptyStackException.class);
    }

    @Test
    public void popShouldReturnLastElementAndRemoveIt() {
        stack.push("bar");
        stack.push("foo");

        expect(stack.pop()).toBe("foo");
        expect(stack).toBe(iterableOf("bar"));

        expect(stack.pop()).toBe("bar");
        expect(stack).toBe(emptyIterable());
    }

    @Test
    public void peekShouldReturnLastElementWithoutRemovingIt() {
        stack.push("bar");
        stack.push("foo");

        expect(stack.peek()).toBe("foo");
        expect(stack).toBe(iterableOf("bar", "foo"));
    }

    @Test
    public void addStackListenerShouldNotAcceptNullArg() {
        expect(() -> stack.addStackListener(null)).toThrow(AssertionError.class);
    }

    @Test
    public void removeStackListenerShouldNotAcceptNullArg() {
        expect(() -> stack.removeStackListener(null)).toThrow(AssertionError.class);
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsPushed() {
        stack.addStackListener(stackListener);
        stack.push("foo");

        verify(stackListener).onElementPushed(eq(stack), eq("foo"));
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsPopped() {
        stack.addStackListener(stackListener);

        stack.push("foo");
        stack.pop();

        final InOrder inOrder = inOrder(stackListener);

        inOrder.verify(stackListener).onElementPushed(eq(stack), eq("foo"));
        inOrder.verify(stackListener).onElementPopped(eq(stack), eq("foo"));
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsInserted() {
        stack.addStackListener(stackListener);

        stack.push("foo");
        stack.insert(0, "bar");

        final InOrder inOrder = inOrder(stackListener);

        inOrder.verify(stackListener).onElementPushed(eq(stack), eq("foo"));
        inOrder.verify(stackListener).onElementInserted(eq(stack), eq("bar"), eq(0));
    }

    @Test
    public void insertShouldInsertElementAtIndex() {
        stack.push("foo");
        stack.insert(0, "bar");

        expect(stack).toBe(iterableOf("bar", "foo"));
    }

    @Test
    public void tailShouldReturnElementsFromIndex() {
        stack.push("foo");
        stack.push("bar");
        stack.push("baz");

        expect(stack.tail(0)).toBe(iterableOf("foo", "bar", "baz"));
        expect(stack.tail(1)).toBe(iterableOf("bar", "baz"));
        expect(stack.tail(2)).toBe(iterableOf("baz"));
        expect(stack.tail(3)).toBe(emptyIterable());
    }

    @Test
    public void tailShouldFailForInvalidIndex() {
        expect(() -> stack.tail(-1)).toThrow(AssertionError.class);
        expect(() -> stack.tail(1)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void clearShouldRemoveAllElements() {
        stack.push("foo");
        stack.push("bar");
        stack.clear();

        expect(stack).toBe(emptyIterable());
        expect(stack.size()).toBe(0);
    }

    @Test
    public void streamShouldStreamAllElements() {
        stack.push("foo");
        stack.push("bar");
        stack.push("baz");

        final String[] strings = stack.stream().toArray(String[]::new);

        expect(strings).toBe(arrayOf("foo", "bar", "baz"));
    }

    @Test
    public void swapShouldFailIfStackIsEmpty() {
        expect(() -> stack.swap("foo")).toThrow(EmptyStackException.class);
    }

    @Test
    public void swapShouldNotAcceptNullElement() {
        stack.push("foo");
        expect(() -> stack.swap(null)).toThrow(AssertionError.class);
    }

    @Test
    public void swapShouldReplaceTopElementAndReturnOld() {
        stack.push("bar");
        stack.push("foo");

        expect(stack.swap("baz")).toBe("foo");
        expect(stack).toBe(iterableOf("bar", "baz"));
    }

    @Test
    public void swapShouldNotifyStackListener() {
        stack.push("foo");
        stack.addStackListener(stackListener);
        stack.swap("bar");

        verify(stackListener).onElementSwapped(eq(stack), eq("foo"), eq("bar"));
    }

}