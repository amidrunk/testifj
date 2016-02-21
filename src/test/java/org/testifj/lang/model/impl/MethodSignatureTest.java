package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.classfile.ClassFileFormatException;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

    @Test(expected = AssertionError.class)
    public void signatureShouldRejectNullMethod() {
        MethodSignature.create(new Type[]{}, void.class).test(null);
    }

    @Test
    public void fromMethodShouldCreateSignature() throws Exception {
        assertEquals("()V", MethodSignature.from(getClass().getMethod("method1")).toString());
        assertEquals("()Ljava/lang/Object;", MethodSignature.from(getClass().getMethod("method2")).toString());
        assertEquals("(ILjava/lang/String;)V", MethodSignature.from(getClass().getMethod("method3", int.class, String.class)).toString());
        assertEquals("(ILjava/lang/String;)Ljava/lang/Object;", MethodSignature.from(getClass().getMethod("method4", int.class, String.class)).toString());
        assertEquals("([I)[Ljava/lang/Object;", MethodSignature.from(getClass().getMethod("method5", int[].class)).toString());
    }

    @Test(expected = AssertionError.class)
    public void fromMethodShouldRejectNullMethod() {
        MethodSignature.from(null);
    }

    @Test
    public void testShouldMatchMethodVoidMethodWithNoParameters() throws Exception  {
        final MethodSignature signature = MethodSignature.create(new Type[]{}, void.class);

        assertTrue(signature.test(getClass().getMethod("method1")));
        assertFalse(signature.test(getClass().getMethod("method2")));
        assertFalse(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method5", int[].class)));
    }

    @Test
    public void testShouldMatchMethodWithNonVoidReturnType() throws Exception {
        final MethodSignature signature = MethodSignature.create(new Type[]{}, Object.class);

        assertFalse(signature.test(getClass().getMethod("method1")));
        assertTrue(signature.test(getClass().getMethod("method2")));
        assertFalse(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method5", int[].class)));
    }

    @Test
    public void testShouldMatchMethodWithVoidReturnTypeAndMultipleParameters() throws Exception {
        final MethodSignature signature = MethodSignature.create(new Type[]{int.class, String.class}, void.class);

        assertFalse(signature.test(getClass().getMethod("method1")));
        assertFalse(signature.test(getClass().getMethod("method2")));
        assertTrue(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method5", int[].class)));
    }

    @Test
    public void testShouldMatchMethodWithObjectReturnTypeAndMultipleParameters() throws Exception {
        final MethodSignature signature = MethodSignature.create(new Type[]{int.class, String.class}, Object.class);

        assertFalse(signature.test(getClass().getMethod("method1")));
        assertFalse(signature.test(getClass().getMethod("method2")));
        assertFalse(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertTrue(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method5", int[].class)));
    }

    @Test
    public void testShouldMatchMethodWithArrayReturnTypeAndArrayParameter() throws Exception {
        final MethodSignature signature = MethodSignature.create(new Type[]{int[].class}, Object[].class);

        assertFalse(signature.test(getClass().getMethod("method1")));
        assertFalse(signature.test(getClass().getMethod("method2")));
        assertFalse(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertTrue(signature.test(getClass().getMethod("method5", int[].class)));
    }

    // Support methods
    //

    public void method1() {}

    public Object method2() {
        return null;
    }

    public void method3(int a, String b) {
    }

    public Object method4(int a, String b) {
        return null;
    }

    public Object[] method5(int[] a) {
        return null;
    }
}
