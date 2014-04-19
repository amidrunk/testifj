package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.ClassFileFormatException;

import java.lang.reflect.Type;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.empty;

@SuppressWarnings("unchecked")
public class MethodSignatureTest {

    @Test
    public void parseShouldNotAcceptNullOrEmptySpec() {
        expect(() -> MethodSignature.parse(null)).toThrow(AssertionError.class);
        expect(() -> MethodSignature.parse("")).toThrow(AssertionError.class);
    }

    @Test
    public void signatureMustBeginWithParentheses() {
        expect(() -> MethodSignature.parse("foo)V")).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void voidMethodWithoutParametersSignatureCanBeParsed() {
        final MethodSignature signature = MethodSignature.parse("()V");

        expect(signature.getParameterTypes()).toBe(empty());
        expect(signature.getReturnType()).toBe((Class) void.class);
    }

    @Test
    public void objectMethodWithObjectReturnCanBeParsed() {
        final MethodSignature signature = MethodSignature.parse("(Ljava/lang/Object;)Ljava/lang/String;");

        expect(signature.getParameterTypes().toArray()).toBe(new Object[]{Object.class});
        expect(signature.getReturnType()).toBe(String.class);
    }

    @Test
    public void objectMethodWithMixedParametersCanBeParsed() {
        final MethodSignature signature = MethodSignature.parse("(ILjava/lang/Object;Ljava/lang/String;)V");

        expect(signature.getParameterTypes().toArray()).toBe(new Object[]{int.class, Object.class, String.class});
        expect(signature.getReturnType()).toBe(void.class);
    }

    @Test
    public void methodWithArraysAsParametersAndReturnTypeCanBeParsed() {
        final MethodSignature signature = MethodSignature.parse("([Ljava/lang/Object;[[I)[Ljava/lang/String;");

        expect(signature.getParameterTypes().toArray()).toBe(new Object[]{Object[].class, int[][].class});
        expect(signature.getReturnType()).toBe(String[].class);
    }

    @Test
    public void createShouldNotAcceptInvalidParameters() {
        expect(() -> MethodSignature.create(new Type[]{}, null)).toThrow(AssertionError.class);
        expect(() -> MethodSignature.create(null, String.class)).toThrow(AssertionError.class);
        expect(() -> MethodSignature.create(new Type[]{String.class, null}, String.class)).toThrow(AssertionError.class);
    }

    @Test
    public void createShouldCreateSignatureFromPrimitives() {
        final MethodSignature actualSignature = MethodSignature.create(new Type[]{
                byte.class,
                short.class,
                char.class,
                int.class,
                long.class,
                float.class,
                double.class,
                boolean.class}, void.class);

        final MethodSignature expectedSignature = MethodSignature.parse("(BSCIJFDZ)V");

        expect(actualSignature).toBe(expectedSignature);
        expect(actualSignature.toString()).toBe(expectedSignature.toString());
    }

    @Test
    public void createShouldCreateSignatureFromObjectsAndPrimitives() {
        final MethodSignature actualSignature = MethodSignature.create(new Type[]{String.class, int.class}, Integer.class);
        final MethodSignature expectedSignature = MethodSignature.parse("(Ljava/lang/String;I)Ljava/lang/Integer;");

        expect(actualSignature).toBe(expectedSignature);
        expect(actualSignature.toString()).toBe(expectedSignature.toString());
    }

    @Test
    public void createShouldCreateSignatureFromObjectArrays() {
        final MethodSignature actualSignature = MethodSignature.create(new Type[]{Object[].class}, Object[][].class);
        final MethodSignature expectedSignature = MethodSignature.parse("([Ljava/lang/Object;)[[Ljava/lang/Object;");

        expect(actualSignature).toBe(expectedSignature);
        expect(actualSignature.toString()).toBe(expectedSignature.toString());
    }

    @Test
    public void createShouldCreateSignatureFromPrimitiveArrays() {
        final MethodSignature actualSignature = MethodSignature.create(new Type[]{int[].class, short[].class}, boolean[][].class);
        final MethodSignature expectedSignature = MethodSignature.parse("([I[S)[[Z");

        expect(actualSignature).toBe(expectedSignature);
        expect(actualSignature.toString()).toBe(expectedSignature.toString());
    }

}
