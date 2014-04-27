package org.testifj.lang.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.*;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Cast;
import org.testifj.lang.model.impl.CastImpl;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.constant;

public class TypeCheckDecompilerExtensionsTest {

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
        expect(() -> TypeCheckDecompilerExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForCheckCast() {
        final DecompilerConfiguration.Builder configurationBuilder = mock(DecompilerConfiguration.Builder.class);

        TypeCheckDecompilerExtensions.configure(configurationBuilder);

        verify(configurationBuilder).extend(eq(ByteCode.checkcast), any(DecompilerExtension.class));
    }

    @Test
    public void checkcastExtensionShouldPushCastOntoStack() throws IOException {
        final DecompilerExtension checkcast = TypeCheckDecompilerExtensions.checkcast();

        when(constantPool.getClassName(eq(1))).thenReturn("java/lang/String");
        when(exampleContext.resolveType(eq("java/lang/String"))).thenReturn(String.class);
        when(exampleContext.pop()).thenReturn(constant("foo"));

        boolean checkcastHandled = checkcast.decompile(exampleContext, CodeStreamTestUtils.codeStream(0, 1), ByteCode.checkcast);

        expect(checkcastHandled).toBe(true);

        verify(exampleContext).push(eq(AST.cast(constant("foo")).to(String.class)));
    }

    @Test
    public void discardImplicitCastShouldPushTargetExpressionOntoStack() throws IOException {
        final DecompilerExtension discardImplicitCast = TypeCheckDecompilerExtensions.discardImplicitCast();
        final Cast cast = AST.cast(AST.constant("foo")).to(String.class);

        when(exampleContext.peek()).thenReturn(cast);
        when(exampleContext.pop()).thenReturn(cast);

        final boolean popInstructionHandled = discardImplicitCast.decompile(exampleContext, CodeStreamTestUtils.codeStream(), ByteCode.pop);

        expect(popInstructionHandled).toBe(true);

        verify(exampleContext).push(eq(AST.constant("foo")));
    }

}
