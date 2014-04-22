package org.testifj.lang;

import org.junit.Test;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.ElementType;

import java.io.PrintWriter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class SimpleCodeGeneratorConfigurationTest {

    private final CodeGeneratorConfiguration configuration = new SimpleCodeGeneratorConfiguration.Builder().build();
    private final CodeGenerationContext context = mock(CodeGenerationContext.class);
    private final Method method = mock(Method.class);
    private final SimpleCodeGeneratorConfiguration.Builder builder = new SimpleCodeGeneratorConfiguration.Builder();
    private final CodeGeneratorExtension extension1 = mock(CodeGeneratorExtension.class);
    private final PrintWriter out = mock(PrintWriter.class);
    private final CodeGeneratorExtension extension2 = mock(CodeGeneratorExtension.class);

    @Test
    public void getExtensionShouldReturnNullIfConfigurationIsEmpty() {
        expect(configuration.getExtension(context, new CodePointerImpl(method, constant(0)))).toBe(equalTo(null));
    }

    @Test
    public void extendInBuilderShouldNotAcceptNullElementTypeOrExtension() {
        expect(() -> builder.extend(null, mock(CodeGeneratorExtension.class))).toThrow(AssertionError.class);
        expect(() -> builder.extend(ElementType.JUMP, null)).toThrow(AssertionError.class);
    }

    @Test
    public void getExtensionShouldNotAcceptNullCodePointer() {
        expect(() -> configuration.getExtension(mock(CodeGenerationContext.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void getExtensionShouldReturnConfiguredExtension() {
        final CodeGeneratorConfiguration configuration = builder
                .extend(ElementType.CONSTANT, extension1)
                .build();

        final CodePointerImpl codePointer = new CodePointerImpl(method, AST.constant(1));
        final CodeGeneratorExtension extension = configuration.getExtension(context, codePointer);

        when(extension1.generateCode(eq(context), eq(codePointer), eq(out))).thenReturn(true);

        expect(extension.generateCode(context, codePointer, out)).toBe(true);

        verify(extension1).generateCode(eq(context), eq(codePointer), eq(out));
    }

    @Test
    public void multipleExtensionsCanBeConfiguredForTheSameElementInPriorityOrder() {
        final CodeGeneratorConfiguration configuration = builder.extend(ElementType.CONSTANT, extension1)
                .extend(ElementType.CONSTANT, extension2)
                .build();

        final CodePointerImpl codePointer = new CodePointerImpl(method, AST.constant(1));
        final CodeGeneratorExtension extension = configuration.getExtension(context, codePointer);

        expect(extension.generateCode(context, codePointer, out)).toBe(false);

        verify(extension1).generateCode(eq(context), eq(codePointer), eq(out));
        verify(extension2).generateCode(eq(context), eq(codePointer), eq(out));
    }

}
