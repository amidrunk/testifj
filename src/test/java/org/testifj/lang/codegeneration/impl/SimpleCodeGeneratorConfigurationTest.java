package org.testifj.lang.codegeneration.impl;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.SimpleCodeGeneratorConfiguration;
import org.testifj.lang.codegeneration.CodeGenerationContext;
import org.testifj.lang.codegeneration.CodeGeneratorConfiguration;
import org.testifj.lang.codegeneration.CodeGeneratorExtension;
import org.testifj.lang.codegeneration.ElementSelector;
import org.testifj.lang.decompile.impl.CodePointerImpl;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.ElementType;

import java.io.PrintWriter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.ElementType.CONSTANT;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

@SuppressWarnings("unchecked")
public class SimpleCodeGeneratorConfigurationTest {

    private final CodeGeneratorConfiguration configuration = new SimpleCodeGeneratorConfiguration.Builder().build();
    private final CodeGenerationContext context = mock(CodeGenerationContext.class);
    private final Method method = mock(Method.class);
    private final SimpleCodeGeneratorConfiguration.Builder builder = new SimpleCodeGeneratorConfiguration.Builder();
    private final PrintWriter out = mock(PrintWriter.class);

    private final CodeGeneratorExtension extension1 = mock(CodeGeneratorExtension.class, "extension1");
    private final CodeGeneratorExtension extension2 = mock(CodeGeneratorExtension.class, "extension2");
    private final CodeGeneratorExtension extension3 = mock(CodeGeneratorExtension.class, "extension3");

    @Test
    public void getExtensionShouldReturnNullIfConfigurationIsEmpty() {
        expect(configuration.getExtension(context, new CodePointerImpl<>(method, constant(0)))).toBe(equalTo(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void extendInBuilderShouldNotAcceptNullElementTypeOrExtension() {
        expect(() -> builder.extend(null, mock(CodeGeneratorExtension.class))).toThrow(AssertionError.class);
        expect(() -> builder.extend(ElementSelector.forType(ElementType.BRANCH), null)).toThrow(AssertionError.class);
    }

    @Test
    public void getExtensionShouldNotAcceptNullCodePointer() {
        expect(() -> configuration.getExtension(mock(CodeGenerationContext.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void getExtensionShouldReturnConfiguredExtension() {
        final CodeGeneratorConfiguration configuration = builder
                .extend(ElementSelector.forType(CONSTANT), extension1)
                .build();

        final CodePointerImpl<Constant> codePointer = new CodePointerImpl<>(method, constant(1));
        final CodeGeneratorExtension extension = configuration.getExtension(context, codePointer);

        extension.call(context, codePointer, out);

        verify(extension1).call(eq(context), eq(codePointer), eq(out));
    }

    @Test
    public void twoExtensionsCanBeConfiguredForTheSameElementWithDifferentSelectors() {
        final CodeGeneratorConfiguration configuration = builder
                .extend(ElementSelector.<Constant>forType(CONSTANT)
                        .where(cp -> cp.getElement().getConstant().equals(1)), extension1)
                .extend(ElementSelector.<Constant>forType(CONSTANT)
                        .where(cp -> cp.getElement().getConstant().equals(2)), extension2)
                .build();

        final CodePointerImpl codePointerWithConstant1 = new CodePointerImpl(method, constant(1));
        final CodePointerImpl codePointerWithConstant2 = new CodePointerImpl(method, constant(2));

        configuration.getExtension(context, codePointerWithConstant1).call(context, codePointerWithConstant1, out);

        verify(extension1).call(eq(context), eq(codePointerWithConstant1), eq(out));
        verify(extension2, times(0)).call(eq(context), eq(codePointerWithConstant1), eq(out));

        configuration.getExtension(context, codePointerWithConstant2).call(context, codePointerWithConstant2, out);

        verifyNoMoreInteractions(extension1);
        verify(extension2).call(eq(context), eq(codePointerWithConstant2), eq(out));
    }

    @Test
    public void threeExtensionsCanBeConfiguredOnTehSameElementWithDifferentSelectors() {
        final CodeGeneratorConfiguration configuration = builder
                .extend(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(1))), extension1)
                .extend(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(2))), extension2)
                .extend(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(3))), extension3)
                .build();

        final CodePointerImpl codePointer1 = new CodePointerImpl<>(method, constant(1));
        final CodePointerImpl codePointer2 = new CodePointerImpl<>(method, constant(2));
        final CodePointerImpl codePointer3 = new CodePointerImpl<>(method, constant(3));

        configuration.getExtension(context, codePointer1).call(context, codePointer1, out);
        configuration.getExtension(context, codePointer2).call(context, codePointer2, out);
        configuration.getExtension(context, codePointer3).call(context, codePointer3, out);

        final InOrder inOrder = Mockito.inOrder(extension1, extension2, extension3);

        inOrder.verify(extension1, times(1)).call(eq(context), eq(codePointer1), eq(out));
        inOrder.verify(extension2, times(1)).call(eq(context), eq(codePointer2), eq(out));
        inOrder.verify(extension3, times(1)).call(eq(context), eq(codePointer3), eq(out));

        verifyNoMoreInteractions(extension1, extension2, extension3);
    }

}
