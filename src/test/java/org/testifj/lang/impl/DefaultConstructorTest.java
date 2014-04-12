package org.testifj.lang.impl;

import org.testifj.lang.Attribute;
import org.junit.Test;
import org.testifj.lang.impl.DefaultConstructor;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.Equal.equal;
import static org.testifj.matchers.core.Not.not;
import static org.testifj.matchers.core.StringShould.containString;

public class DefaultConstructorTest {

    private final DefaultConstructor constructor = new DefaultConstructor(1234, "<init>", "()V", new Attribute[]{});

    @Test
    public void constructorShouldNotAcceptNullName() {
        expect(() -> new DefaultConstructor(0, null, "()V", new Attribute[]{})).toThrow(AssertionError.class);
    }

    @Test
    public void nameMustBeConstructorName() {
        expect(() -> new DefaultConstructor(0, "foo", "V()", new Attribute[]{}))
                .toThrow(AssertionError.class)
                .where((e) -> e.getMessage().contains("<init>"));
    }

    @Test
    public void constructorShouldRetainArguments() {
        expect(constructor.getAccessFlags()).to(equal(1234));
        expect(constructor.getName()).to(equal("<init>"));
        expect(constructor.getSignature()).to(equal("()V"));
        expect(constructor.getAttributes()).to(equal(Arrays.asList(new Attribute[]{})));
    }

    @Test
    public void signatureCannotBeNull() {
        expect(() -> new DefaultConstructor(0, "<init>", null, new Attribute[]{})).toThrow(AssertionError.class);
    }

    @Test
    public void attributesCannotBeNull() {
        expect(() -> new DefaultConstructor(0, "<init>", "()V", null)).toThrow(AssertionError.class);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(constructor).to(equal(constructor));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(constructor).to(not(equal(null)));
        expect((Object) constructor).to(not(equal("foo")));
    }

    @Test
    public void equalConstructorsShouldBeEqual() {
        final DefaultConstructor other = new DefaultConstructor(1234, "<init>", "()V", new Attribute[]{});

        expect(constructor).to(equal(other));
        expect(constructor.hashCode()).to(equal(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        expect(constructor.toString()).to(containString("12354"));
        expect(constructor.toString()).to(containString("<init>"));
        expect(constructor.toString()).to(containString("()V"));
    }

}