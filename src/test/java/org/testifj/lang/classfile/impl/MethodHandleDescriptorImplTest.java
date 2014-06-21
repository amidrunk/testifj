package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.ReferenceKind;
import org.testifj.lang.classfile.impl.MethodHandleDescriptorImpl;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class MethodHandleDescriptorImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new MethodHandleDescriptorImpl(null, "Foo", "bar", "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, null, "bar", "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "", "bar", "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", null, "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", "", "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", "bar", null)).toThrow(AssertionError.class);
        expect(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", "bar", "")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        given(new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", "bar", "()V")).then(descriptor -> {
            expect(descriptor.getReferenceKind()).toBe(ReferenceKind.GET_FIELD);
            expect(descriptor.getClassName()).toBe("Foo");
            expect(descriptor.getMethodName()).toBe("bar");
            expect(descriptor.getMethodDescriptor()).toBe("()V");
        });
    }
}
