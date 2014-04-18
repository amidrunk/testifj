package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.Attribute;
import org.testifj.lang.ClassFile;
import org.testifj.lang.model.impl.SignatureImpl;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;
import static org.testifj.matchers.core.Not.not;
import static org.testifj.matchers.core.StringShould.containString;

@SuppressWarnings("unchecked")
public class DefaultConstructorTest {

    private final Supplier<ClassFile> classFileSupplier = mock(Supplier.class);

    private final DefaultConstructor constructor = new DefaultConstructor(classFileSupplier, 1234, "<init>", SignatureImpl.parse("()V"), new Attribute[]{});

    @Test
    public void constructorShouldNotAcceptNullClassFileSupplier() {
        expect(() -> new DefaultConstructor(null, 0, "foo", SignatureImpl.parse("()V"), new Attribute[]{})).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullName() {
        expect(() -> new DefaultConstructor(classFileSupplier, 0, null, SignatureImpl.parse("()V"), new Attribute[]{})).toThrow(AssertionError.class);
    }

    @Test
    public void nameMustBeConstructorName() {
        expect(() -> new DefaultConstructor(classFileSupplier, 0, "foo", SignatureImpl.parse("()V"), new Attribute[]{}))
                .toThrow(AssertionError.class)
                .where((e) -> e.getMessage().contains("<init>"));
    }

    @Test
    public void constructorShouldRetainArguments() {
        final ClassFile classFile = mock(ClassFile.class);
        when(classFileSupplier.get()).thenReturn(classFile);

        expect(constructor.getClassFile()).toBe(classFile);
        expect(constructor.getAccessFlags()).toBe(1234);
        expect(constructor.getName()).toBe("<init>");
        expect(constructor.getSignature()).toBe(SignatureImpl.parse("()V"));
        expect(constructor.getAttributes()).toBe(Arrays.asList(new Attribute[]{}));
    }

    @Test
    public void signatureCannotBeNull() {
        expect(() -> new DefaultConstructor(classFileSupplier, 0, "<init>", null, new Attribute[]{})).toThrow(AssertionError.class);
    }

    @Test
    public void attributesCannotBeNull() {
        expect(() -> new DefaultConstructor(classFileSupplier, 0, "<init>", SignatureImpl.parse("()V"), null)).toThrow(AssertionError.class);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(constructor).toBe(constructor);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(constructor).to(not(equal(null)));
        expect((Object) constructor).to(not(equal((Object) "foo")));
    }

    @Test
    public void equalConstructorsShouldBeEqual() {
        final DefaultConstructor other = new DefaultConstructor(classFileSupplier, 1234, "<init>", SignatureImpl.parse("()V"), new Attribute[]{});

        expect(constructor).toBe(other);
        expect(constructor.hashCode()).toBe(other.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        expect(constructor.toString()).to(containString("1234"));
        expect(constructor.toString()).to(containString("<init>"));
        expect(constructor.toString()).to(containString("()V"));
    }

}
