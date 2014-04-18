package org.testifj.lang.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class NameAndTypeDescriptorImplTest {

    private final NameAndTypeDescriptorImpl descriptor = new NameAndTypeDescriptorImpl("foo", "bar");

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new NameAndTypeDescriptorImpl(null, "I")).toThrow(AssertionError.class);
        expect(() -> new NameAndTypeDescriptorImpl("", "I")).toThrow(AssertionError.class);
        expect(() -> new NameAndTypeDescriptorImpl("foo", null)).toThrow(AssertionError.class);
        expect(() -> new NameAndTypeDescriptorImpl("foo", "")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        expect(descriptor.getName()).toBe("foo");
        expect(descriptor.getDescriptor()).toBe("bar");
    }

    @Test
    public void descriptorShouldBeEqualToItSelf() {
        expect(descriptor).toBe(equalTo(descriptor));
    }

    @Test
    public void descriptorShouldNotBeEqualToNullOrInCorrectType() {
        expect((Object) descriptor).not().toBe(equalTo("foo"));
        expect(descriptor).not().toBe(equalTo(null));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        expect(descriptor.toString()).to(containString("foo"));
        expect(descriptor.toString()).to(containString("bar"));
    }

}
