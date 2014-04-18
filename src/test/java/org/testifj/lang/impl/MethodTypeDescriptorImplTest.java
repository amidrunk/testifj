package org.testifj.lang.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class MethodTypeDescriptorImplTest {

    @Test
    public void constructorShouldNotAcceptNullOrEmptyDescriptor() {
        expect(() -> new MethodTypeDescriptorImpl(null)).toThrow(AssertionError.class);
        expect(() -> new MethodTypeDescriptorImpl("")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainDescriptor() {
        given(new MethodTypeDescriptorImpl("()V")).then(descriptor -> {
            expect(descriptor.getDescriptor()).toBe("()V");
        });
    }

}
