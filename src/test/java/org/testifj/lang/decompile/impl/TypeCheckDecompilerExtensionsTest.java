package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.ConstantPool;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerExtension;
import org.testifj.lang.decompile.impl.DecompilerConfigurationImpl;
import org.testifj.lang.decompile.impl.TypeCheckDecompilerExtensions;
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
        final DecompilerConfiguration.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();

        TypeCheckDecompilerExtensions.configure(configurationBuilder);

        given(configurationBuilder.build()).then(it -> {
            expect(it.getDecompilerExtension(mock(DecompilationContext.class), ByteCode.checkcast)).not().toBe(equalTo(null));
        });
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