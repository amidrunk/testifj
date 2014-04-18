package org.testifj.lang.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class FieldRefDescriptorImplTest {

    @Test
    public void constructorShouldValidateArguments() {
        expect(() -> new FieldRefDescriptorImpl(null, "I", "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldRefDescriptorImpl("", "I", "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldRefDescriptorImpl("java/lang/String", null, "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldRefDescriptorImpl("java/lang/String", "", "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldRefDescriptorImpl("java/lang/String", "I", null)).toThrow(AssertionError.class);
        expect(() -> new FieldRefDescriptorImpl("java/lang/String", "I", "")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final FieldRefDescriptorImpl fieldDescriptor = new FieldRefDescriptorImpl("java/lang/String", "I", "foo");

        expect(fieldDescriptor.getClassName()).toBe("java/lang/String");
        expect(fieldDescriptor.getDescriptor()).toBe("I");
        expect(fieldDescriptor.getName()).toBe("foo");
    }

}