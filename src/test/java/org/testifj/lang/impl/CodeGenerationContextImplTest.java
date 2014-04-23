package org.testifj.lang.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.CodeGenerationContext;
import org.testifj.lang.CodeGenerationDelegate;
import org.testifj.lang.CodePointer;
import org.testifj.lang.CodeStyle;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;

public class CodeGenerationContextImplTest {

    private final CodeGenerationDelegate codeGenerationDelegate = mock(CodeGenerationDelegate.class);

    private final CodeStyle codeStyle = mock(CodeStyle.class);

    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(codeGenerationDelegate, codeStyle);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new CodeGenerationContextImpl(null, codeStyle)).toThrow(AssertionError.class);
        expect(() -> new CodeGenerationContextImpl(codeGenerationDelegate, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainCodeStyle() {
        expect(context.getCodeStyle()).toBe(codeStyle);
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
