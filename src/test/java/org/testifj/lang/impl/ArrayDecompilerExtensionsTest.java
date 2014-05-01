package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.LocalVariableReference;
import org.testifj.lang.model.impl.ArrayLoadImpl;
import org.testifj.lang.model.impl.ArrayStoreImpl;
import org.testifj.lang.model.impl.FieldReferenceImpl;
import org.testifj.lang.model.impl.NewArrayImpl;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;

public class ArrayDecompilerExtensionsTest {

    private final DecompilationContext context = mock(DecompilationContext.class);

    private final CodeStream code = mock(CodeStream.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> ArrayDecompilerExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForArrayInstructions() {
        final DecompilerConfiguration.Builder configurationBuilder = mock(DecompilerConfiguration.Builder.class);

        ArrayDecompilerExtensions.configure(configurationBuilder);

        verify(configurationBuilder).extend(eq(ByteCode.anewarray), any());
        verify(configurationBuilder).extend(eq(ByteCode.aaload), any());
        verify(configurationBuilder).extend(eq(ByteCode.aastore), any());
        verify(configurationBuilder).extend(eq(ByteCode.arraylength), any());
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

        boolean aaloadHandled = ArrayDecompilerExtensions.aaload().decompile(context, code, ByteCode.aaload);

        expect(aaloadHandled).toBe(true);

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

}
