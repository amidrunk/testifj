package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.ClassFileFormatException;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.empty;
import static org.testifj.matchers.core.Equal.equal;

@SuppressWarnings("unchecked")
public class SignatureImplTest {

    @Test
    public void parseShouldNotAcceptNullOrEmptySpec() {
        expect(() -> SignatureImpl.parse(null)).toThrow(AssertionError.class);
        expect(() -> SignatureImpl.parse("")).toThrow(AssertionError.class);
    }

    @Test
    public void signatureMustBeginWithParentheses() {
        expect(() -> SignatureImpl.parse("foo)V")).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void voidMethodWithoutParametersSignatureCanBeParsed() {
        final SignatureImpl signature = SignatureImpl.parse("()V");

        expect(signature.getParameterTypes()).toBe(empty());
        expect(signature.getReturnType()).to(equal((Class) void.class));
    }

    @Test
    public void objectMethodWithObjectReturnCanBeParsed() {
        final SignatureImpl signature = SignatureImpl.parse("(Ljava/lang/Object;)Ljava/lang/String;");

        expect(signature.getParameterTypes().toArray()).toBe(new Object[]{Object.class});
        expect(signature.getReturnType()).toBe(String.class);
    }

    @Test
    public void objectMethodWithMixedParametersCanBeParsed() {
        final SignatureImpl signature = SignatureImpl.parse("(ILjava/lang/Object;Ljava/lang/String;)V");

        expect(signature.getParameterTypes().toArray()).toBe(new Object[]{int.class, Object.class, String.class});
        expect(signature.getReturnType()).toBe(void.class);
    }

    @Test
    public void methodWithArraysAsParametersAndReturnTypeCanBeParsed() {
        final SignatureImpl signature = SignatureImpl.parse("([Ljava/lang/Object;[[I)[Ljava/lang/String;");

        expect(signature.getParameterTypes().toArray()).toBe(new Object[]{Object[].class, int[][].class});
        expect(signature.getReturnType()).toBe(String[].class);
    }
}
