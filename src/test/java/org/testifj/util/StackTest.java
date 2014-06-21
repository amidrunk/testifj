package org.testifj.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.function.Supplier;

import static org.junit.runners.Parameterized.Parameters;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.CollectionThatIs.empty;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

@RunWith(Parameterized.class)
public class StackTest {

    @Parameters
    public static Iterable<Object[]> stacks() {
        return Arrays.<Object[]>asList(
            new Object[] { (Supplier) Stack::new},
            new Object[] { (Supplier) () -> new Stack<>(new LinkedList<>()) }
        );
    }

    private final Stack<String> stack;

    public StackTest(Supplier<Stack<String>> stackSupplier) {
        this.stack = stackSupplier.get();
    }

    @Test
    public void constructorShouldNotAcceptNullBackingList() {
        expect(() -> new Stack<String>(null)).toThrow(AssertionError.class);
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
        final Stack<String> other = new Stack<>();

        stack.add("foo");
        other.add("foo");

        expect(stack).toBe(equalTo(other));
        expect(stack.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringShouldReturnStringWithContents() {
        stack.add("foo");
        stack.add("bar");

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
        expect(stack).toBe(collectionOf("bar"));

        expect(stack.pop()).toBe("bar");
        expect(stack).toBe(empty());
    }

    @Test
    public void peekShouldReturnLastElementWithoutRemovingIt() {
        stack.push("bar");
        stack.push("foo");

        expect(stack.peek()).toBe("foo");
        expect(stack).toBe(collectionOf("bar", "foo"));
    }

}