package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerExtension;
import org.testifj.lang.decompile.impl.ArrayDecompilerExtensions;
import org.testifj.lang.decompile.impl.DecompilerConfigurationImpl;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.LocalVariableReference;
import org.testifj.lang.model.impl.*;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ArrayDecompilerExtensionsTest {

    private final DecompilationContext context = mock(DecompilationContext.class);

    private final CodeStream code = mock(CodeStream.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> ArrayDecompilerExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForArrayInstructions() {
        final DecompilerConfiguration.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();

        ArrayDecompilerExtensions.configure(configurationBuilder);

        given(configurationBuilder.build()).then(it -> {
            expect(it.getDecompilerExtension(context, ByteCode.anewarray)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(context, ByteCode.aaload)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(context, ByteCode.aastore)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(context, ByteCode.arraylength)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(context, ByteCode.iaload)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void objectArrayStoreShouldEnlistNewArrayStoreOnReferencedArray() throws IOException {
        final Expression value = mock(Expression.class, "value");
        final Expression index = mock(Expression.class, "index");
        final Expression array = mock(Expression.class, "array");

        when(context.pop()).thenReturn(value, index, array);

        ArrayDecompilerExtensions.aastore().decompile(context, code, ByteCode.aastore);

        verify(context).enlist(eq(new ArrayStoreImpl(array, index, value)));
    }

    @Test
    public void aaloadShouldPushObjectArrayAccessOntoStack() throws IOException {
        final Expression index = AST.constant(1);
        final Expression array = AST.local("myArray", String[].class, 1);

        when(context.pop()).thenReturn(index, array);

        ArrayDecompilerExtensions.aaload().decompile(context, code, ByteCode.aaload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, String.class)));
    }

    @Test
    public void newarrayShouldFailIfTypeCodeIsInvalid() throws IOException {
        when(code.nextUnsignedByte()).thenReturn(123);

        expect(() -> ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void newarrayCanPushBooleanArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(4);

        ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(boolean[].class, boolean.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushCharArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(5);

        ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(char[].class, char.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushFloatArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(6);

        ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(float[].class, float.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushDoubleArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(7);

        ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(double[].class, double.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushByteArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(8);

        ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(byte[].class, byte.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushShortArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(9);

        ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(short[].class, short.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushIntArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(10);

        ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(int[].class, int.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushLongArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(11);

        ArrayDecompilerExtensions.newarray().decompile(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(long[].class, long.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void iastoreShouldStoreIntoArrayElement() throws IOException {
        final Constant value = AST.constant(1);
        final Constant index = AST.constant(2);
        final LocalVariableReference array = AST.local("myArray", int[].class, 1);

        when(context.pop()).thenReturn(value, index, array);

        ArrayDecompilerExtensions.iastore().decompile(context, code, ByteCode.iastore);

        verify(context).enlist(eq(new ArrayStoreImpl(array, index, value)));
    }

    @Test
    public void arraylengthShouldPushFieldReferenceOntoStack() throws Exception {
        final LocalVariableReference array = AST.local("foo", String[].class, 1);

        when(context.pop()).thenReturn(array);

        ArrayDecompilerExtensions.arraylength().decompile(context, code, ByteCode.arraylength);

        verify(context).push(new FieldReferenceImpl(array, String[].class, int.class, "length"));
    }

    @Test
    public void integerArrayElementCanBeRetrieved() throws IOException {
        final DecompilerExtension iaload = ArrayDecompilerExtensions.iaload();

        when(context.pop()).thenReturn(
                AST.constant(1),
                AST.local("myArray", int[].class, 1));

        iaload.decompile(context, mock(CodeStream.class), ByteCode.iaload);

        verify(context).push(eq(new ArrayLoadImpl(AST.local("myArray", int[].class, 1), AST.constant(1), int.class)));
    }

    @Test
    public void laloadShouldLoadElementFromArray() throws IOException {
        final DecompilerExtension extension = ArrayDecompilerExtensions.laload();

        final Expression index = mock(Expression.class);
        final Expression array = mock(LocalVariableReference.class);

        when(context.pop()).thenReturn(index, array);

        extension.decompile(context, code, ByteCode.laload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, long.class)));
    }

    @Test
    public void faloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(LocalVariableReference.class);

        when(context.pop()).thenReturn(index, array);

        ArrayDecompilerExtensions.faload().decompile(context, code, ByteCode.faload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, float.class)));
    }

    @Test
    public void daloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(Expression.class);

        when(context.pop()).thenReturn(index, array);

        ArrayDecompilerExtensions.daload().decompile(context, code, ByteCode.daload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, double.class)));
    }

    @Test
    public void baloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(Expression.class);

        when(context.pop()).thenReturn(index, array);

        ArrayDecompilerExtensions.baload().decompile(context, code, ByteCode.baload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, boolean.class)));
    }

    @Test
    public void caloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(Expression.class);

        when(context.pop()).thenReturn(index, array);

        ArrayDecompilerExtensions.caload().decompile(context, code, ByteCode.caload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, char.class)));
    }

    @Test
    public void saloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(Expression.class);

        when(context.pop()).thenReturn(index, array);

        ArrayDecompilerExtensions.saload().decompile(context, code, ByteCode.saload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, short.class)));
    }
}
