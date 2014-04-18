package org.testifj.lang.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class InterfaceMethodRefDescriptorImplTest {

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new InterfaceMethodRefDescriptorImpl(null, "foo", "()V")).toThrow(AssertionError.class);
        expect(() -> new InterfaceMethodRefDescriptorImpl("", "foo", "()V")).toThrow(AssertionError.class);
        expect(() -> new InterfaceMethodRefDescriptorImpl("foo", null, "()V")).toThrow(AssertionError.class);
        expect(() -> new InterfaceMethodRefDescriptorImpl("foo", "", "()V")).toThrow(AssertionError.class);
        expect(() -> new InterfaceMethodRefDescriptorImpl("foo", "bar", null)).toThrow(AssertionError.class);
        expect(() -> new InterfaceMethodRefDescriptorImpl("foo", "bar", "")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final InterfaceMethodRefDescriptorImpl descriptor = new InterfaceMethodRefDescriptorImpl("foo", "bar", "()V");

        expect(descriptor.getClassName()).toBe("foo");
        expect(descriptor.getMethodName()).toBe("bar");
        expect(descriptor.getDescriptor()).toBe("()V");
    }

}
