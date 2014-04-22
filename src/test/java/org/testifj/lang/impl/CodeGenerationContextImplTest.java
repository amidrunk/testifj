package org.testifj.lang.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.CodeGenerationContext;
import org.testifj.lang.CodeGenerationDelegate;
import org.testifj.lang.CodePointer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;

public class CodeGenerationContextImplTest {

    private final CodeGenerationDelegate codeGenerationDelegate = mock(CodeGenerationDelegate.class);

    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(codeGenerationDelegate);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new CodeGenerationContextImpl(null)).toThrow(AssertionError.class);
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
