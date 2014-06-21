package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.decompile.DecompilationStateSelector;
import org.testifj.lang.decompile.impl.DecompilerDelegateAdapter;
import org.testifj.util.Priority;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class DecompilerDelegateAdapterTest {

    private final DecompilationStateSelector decompilationStateSelector = mock(DecompilationStateSelector.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new DecompilerDelegateAdapter<String>(0, null, decompilationStateSelector, "foo")).toThrow(AssertionError.class);
        expect(() -> new DecompilerDelegateAdapter<String>(0, Priority.DEFAULT, null, "foo")).toThrow(AssertionError.class);
        expect(() -> new DecompilerDelegateAdapter<String>(0, Priority.DEFAULT, decompilationStateSelector, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        given(new DecompilerDelegateAdapter<>(123, Priority.HIGH, decompilationStateSelector, "foo")).then(it -> {
            expect(it.getByteCode()).toBe(123);
            expect(it.getPriority()).toBe(Priority.HIGH);
            expect(it.getDecompilationStateSelector()).toBe(decompilationStateSelector);
            expect(it.getDelegate()).toBe("foo");
        });
    }

}