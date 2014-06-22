package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Cast;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class CastInstructionsTest {

    private final Method exampleMethod = mock(Method.class);

    private final ClassFile exampleClassFile = mock(ClassFile.class);

    private final ConstantPool constantPool = mock(ConstantPool.class);

    private final DecompilationContext exampleContext = mock(DecompilationContext.class);

    @Before
    public void setup() {
        when(exampleContext.getMethod()).thenReturn(exampleMethod);
        when(exampleMethod.getClassFile()).thenReturn(exampleClassFile);
        when(exampleClassFile.getConstantPool()).thenReturn(constantPool);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> new CastInstructions().configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForCheckCast() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(mock(DecompilationContext.class), ByteCode.checkcast)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void checkcastExtensionShouldPushCastOntoStack() throws IOException {
        final DecompilerDelegate checkcast = CastInstructions.checkcast();

        when(constantPool.getClassName(eq(1))).thenReturn("java/lang/String");
        when(exampleContext.resolveType(eq("java/lang/String"))).thenReturn(String.class);
        when(exampleContext.pop()).thenReturn(constant("foo"));

        checkcast.apply(exampleContext, CodeStreamTestUtils.codeStream(0, 1), ByteCode.checkcast);

        verify(exampleContext).push(eq(AST.cast(constant("foo")).to(String.class)));
    }

    @Test
    public void discardImplicitCastShouldPushTargetExpressionOntoStack() throws IOException {
        final DecompilerDelegate discardImplicitCast = CastInstructions.discardImplicitCast();
        final Cast cast = AST.cast(AST.constant("foo")).to(String.class);

        when(exampleContext.peek()).thenReturn(cast);
        when(exampleContext.pop()).thenReturn(cast);

        discardImplicitCast.apply(exampleContext, CodeStreamTestUtils.codeStream(), ByteCode.pop);

        verify(exampleContext).push(eq(AST.constant("foo")));
    }

    @Test
    public void supportForIntToByteInstructionShouldBeConfigured() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(exampleContext, ByteCode.i2b)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void int2byteShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2b);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(byte.class)));
    }

    @Test
    public void int2charShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2c);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(char.class)));
    }

    @Test
    public void int2shortShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2s);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(short.class)));
    }

    @Test
    public void int2longShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2l);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(long.class)));
    }

    @Test
    public void int2floatShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2f);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(float.class)));
    }

    @Test
    public void int2doubleShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2d);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(double.class)));
    }

    @Test
    public void long2intShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678L));

        execute(ByteCode.l2i);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678L)).to(int.class)));
    }

    @Test
    public void long2floatShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678L));

        execute(ByteCode.l2f);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678L)).to(float.class)));
    }

    @Test
    public void long2doubleShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678L));

        execute(ByteCode.l2d);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678L)).to(double.class)));
    }

    @Test
    public void float2intShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(1234f));

        execute(ByteCode.f2i);

        verify(exampleContext).push(eq(AST.cast(constant(1234f)).to(int.class)));
    }

    @Test
    public void float2longShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(1234f));

        execute(ByteCode.f2l);

        verify(exampleContext).push(eq(AST.cast(constant(1234f)).to(long.class)));
    }

    private void execute(int byteCode) throws IOException {
        configuration().getDecompilerExtension(exampleContext, byteCode).apply(exampleContext, mock(CodeStream.class), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfiguration.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();

        new CastInstructions().configure(configurationBuilder);

        return configurationBuilder.build();
    }

}
