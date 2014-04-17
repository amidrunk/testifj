package org.testifj.lang.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class FieldDescriptorImplTest {

    @Test
    public void constructorShouldValidateArguments() {
        expect(() -> new FieldDescriptorImpl(null, "I", "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldDescriptorImpl("", "I", "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldDescriptorImpl("java/lang/String", null, "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldDescriptorImpl("java/lang/String", "", "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldDescriptorImpl("java/lang/String", "I", null)).toThrow(AssertionError.class);
        expect(() -> new FieldDescriptorImpl("java/lang/String", "I", "")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final FieldDescriptorImpl fieldDescriptor = new FieldDescriptorImpl("java/lang/String", "I", "foo");

        expect(fieldDescriptor.getClassName()).toBe("java/lang/String");
        expect(fieldDescriptor.getDescriptor()).toBe("I");
        expect(fieldDescriptor.getName()).toBe("foo");
    }

}