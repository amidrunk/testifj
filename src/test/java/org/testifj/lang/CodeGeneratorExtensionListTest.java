package org.testifj.lang;

import org.junit.Test;
import org.testifj.CodeGenerationContext;
import org.testifj.CodePointer;

import java.io.PrintWriter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;

public class CodeGeneratorExtensionListTest {

    private final CodeGeneratorExtension extension1 = mock(CodeGeneratorExtension.class);
    private final CodeGeneratorExtension extension2 = mock(CodeGeneratorExtension.class);
    private final CodeGenerationContext context = mock(CodeGenerationContext.class);
    private final CodePointer codePointer = mock(CodePointer.class);

    @Test
    public void constructorShouldNotAcceptAnyNullExtension() {
        expect(() -> new CodeGeneratorExtensionList(null, extension1)).toThrow(AssertionError.class);
        expect(() -> new CodeGeneratorExtensionList(extension1, extension2, null)).toThrow(AssertionError.class);
    }

    @Test
    public void generateShouldCallAllExtensionsAndReturnFalseIfNoExtensionsAreApplicable() {
        final CodeGeneratorExtensionList list = new CodeGeneratorExtensionList(extension1, extension2);
        final PrintWriter out = mock(PrintWriter.class);

        expect(list.generateCode(context, codePointer, out)).toBe(false);

        verify(extension1).generateCode(eq(context), eq(codePointer), eq(out));
        verify(extension2).generateCode(eq(context), eq(codePointer), eq(out));
    }

    @Test
    public void generateShouldAbortAtFirstMatchingExtensionAndReturnTrue() {
        final CodeGeneratorExtensionList list = new CodeGeneratorExtensionList(extension1, extension2);
        final PrintWriter out = mock(PrintWriter.class);

        when(extension1.generateCode(eq(context), eq(codePointer), eq(out))).thenReturn(true);

        expect(list.generateCode(context, codePointer, out)).toBe(true);

        verify(extension1).generateCode(eq(context), eq(codePointer), eq(out));

        verifyZeroInteractions(extension2);
    }

}
