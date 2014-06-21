package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.impl.MethodRefDescriptorImpl;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class MethodRefDescriptorImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new MethodRefDescriptorImpl(null, "bar", "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodRefDescriptorImpl("", "bar", "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodRefDescriptorImpl("Foo", null, "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodRefDescriptorImpl("Foo", "", "()V")).toThrow(AssertionError.class);
        expect(() -> new MethodRefDescriptorImpl("Foo", "bar", null)).toThrow(AssertionError.class);
        expect(() -> new MethodRefDescriptorImpl("Foo", "bar", "")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        given(new MethodRefDescriptorImpl("Foo", "bar", "()V")).then(descriptor -> {
            expect(descriptor.getClassName()).toBe("Foo");
            expect(descriptor.getMethodName()).toBe("bar");
            expect(descriptor.getDescriptor()).toBe("()V");
        });
    }

    @Test
    public void methodRefDescriptorShouldBeEqualToItSelf() {
        given(new MethodRefDescriptorImpl("Foo", "bar", "()V")).then(d -> {
            expect(d.equals(d)).toBe(true);
            expect(d.hashCode()).toBe(d.hashCode());
        });
    }

    @Test
    public void methodRefDescriptorShouldNotBeEqualToNullOrDifferentType() {
        given(new MethodRefDescriptorImpl("Foo", "bar", "()V")).then(d -> {
            expect(d.equals(null)).toBe(false);
            expect(d.equals("foo")).toBe(false);
        });
    }

    @Test
    public void methodRefDescriptorsWithEqualPropertiesShouldBeEqual() {
        final MethodRefDescriptorImpl descriptor1 = new MethodRefDescriptorImpl("Foo", "bar", "()V");
        final MethodRefDescriptorImpl descriptor2 = new MethodRefDescriptorImpl("Foo", "bar", "()V");

        expect(descriptor1).toBe(equalTo(descriptor2));
        expect(descriptor1.hashCode()).toBe(equalTo(descriptor2.hashCode()));
    }
}

