package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ClassFileResolver;
import org.testifj.lang.decompile.*;
import org.testifj.lang.decompile.impl.CodeGenerationContextImpl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;

public class CodeGenerationContextImplTest {

    private final CodeGenerationDelegate codeGenerationDelegate = mock(CodeGenerationDelegate.class);

    private final TypeResolver typeResolver = mock(TypeResolver.class);

    private final ClassFileResolver classFileResolver = mock(ClassFileResolver.class);

    private final CodeStyle codeStyle = mock(CodeStyle.class);

    private final Decompiler decompiler = mock(Decompiler.class);
    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(
            codeGenerationDelegate,
            typeResolver,
            classFileResolver,
            decompiler,
            codeStyle);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new CodeGenerationContextImpl(null, typeResolver, classFileResolver, decompiler, codeStyle)).toThrow(AssertionError.class);
        expect(() -> new CodeGenerationContextImpl(codeGenerationDelegate, null, classFileResolver, decompiler, codeStyle)).toThrow(AssertionError.class);
        expect(() -> new CodeGenerationContextImpl(codeGenerationDelegate, typeResolver, null, decompiler, codeStyle)).toThrow(AssertionError.class);
        expect(() -> new CodeGenerationContextImpl(codeGenerationDelegate, typeResolver, classFileResolver, null, codeStyle)).toThrow(AssertionError.class);
        expect(() -> new CodeGenerationContextImpl(codeGenerationDelegate, typeResolver, classFileResolver, decompiler, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainCodeStyleAndDependencies() {
        expect(context.getCodeStyle()).toBe(codeStyle);
        expect(context.getClassFileResolver()).toBe(classFileResolver);
        expect(context.getTypeResolver()).toBe(typeResolver);
        expect(context.getDecompiler()).toBe(decompiler);
    }

    @Test
    public void indentationShouldInitiallyBeZero() {
        expect(context.getIndentationLevel()).toBe(0);
    }

    @Test
    public void delegateShouldNotAcceptNullArgument() {
        expect(() -> context.delegate(null)).toThrow(AssertionError.class);
    }

    @Test
    public void delegateShouldCallCodeGenerationDelegate() {
        final CodePointer codePointer = mock(CodePointer.class);

        context.delegate(codePointer);

        verify(codeGenerationDelegate).delegate(eq(context), eq(codePointer));
    }

    @Test
    public void subSectionShouldReturnContextWithIncreasedIndentation() {
        final CodeGenerationContext newContext = context.subSection();

        expect(newContext.getIndentationLevel()).toBe(1);
    }

}
