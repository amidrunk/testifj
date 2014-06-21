package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.impl.InvokeDynamicDescriptorImpl;

import static org.testifj.Expect.expect;

public class InvokeDynamicDescriptorImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new InvokeDynamicDescriptorImpl(0, null, "foo")).toThrow(AssertionError.class);
        expect(() -> new InvokeDynamicDescriptorImpl(0, "", "foo")).toThrow(AssertionError.class);
        expect(() -> new InvokeDynamicDescriptorImpl(0, "foo", null)).toThrow(AssertionError.class);
        expect(() -> new InvokeDynamicDescriptorImpl(0, "foo", "")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final InvokeDynamicDescriptorImpl descriptor = new InvokeDynamicDescriptorImpl(1234, "foo", "()V");

        expect(descriptor.getBootstrapMethodAttributeIndex()).toBe(1234);
        expect(descriptor.getMethodName()).toBe("foo");
        expect(descriptor.getMethodDescriptor()).toBe("()V");
    }

}
