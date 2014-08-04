package org.testifj.lang.codegeneration.impl;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.SimpleCodeGeneratorConfiguration;
import org.testifj.lang.codegeneration.*;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.decompile.impl.CodePointerImpl;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.Element;
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

    private final CodeGeneratorConfiguration emptyConfiguration = SimpleCodeGeneratorConfiguration.configurer().configuration();
    private final CodeGenerationContext context = mock(CodeGenerationContext.class);
    private final Method method = mock(Method.class);
    private final CodeGeneratorConfigurer configurer = SimpleCodeGeneratorConfiguration.configurer();
    private final PrintWriter out = mock(PrintWriter.class);

    private final CodeGeneratorDelegate extension1 = mock(CodeGeneratorDelegate.class, "extension1");
    private final CodeGeneratorDelegate extension2 = mock(CodeGeneratorDelegate.class, "extension2");
    private final CodeGeneratorDelegate extension3 = mock(CodeGeneratorDelegate.class, "extension3");

    @Test
    public void getExtensionShouldReturnNullIfConfigurationIsEmpty() {
        expect(emptyConfiguration.getDelegate(context, new CodePointerImpl<>(method, constant(0)))).toBe(equalTo(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void extendInBuilderShouldNotAcceptNullElementTypeOrExtension() {
        expect(() -> configurer.on(null).then(mock(CodeGeneratorDelegate.class))).toThrow(AssertionError.class);
        expect(() -> configurer.on(ElementSelector.forType(ElementType.BRANCH)).then(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getExtensionShouldNotAcceptNullCodePointer() {
        expect(() -> emptyConfiguration.getDelegate(mock(CodeGenerationContext.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void getExtensionShouldReturnConfiguredExtension() {
        final CodeGeneratorConfiguration configuration = configurer
                .on(ElementSelector.forType(CONSTANT)).then(extension1)
                .configuration();

        final CodePointerImpl<Constant> codePointer = new CodePointerImpl<>(method, constant(1));
        final CodeGeneratorDelegate extension = configuration.getDelegate(context, codePointer);

        extension.apply(context, codePointer, out);

        verify(extension1).apply(eq(context), eq(codePointer), eq(out));
    }

    @Test
    public void twoExtensionsCanBeConfiguredForTheSameElementWithDifferentSelectors() {
        final CodeGeneratorConfiguration configuration = configurer
                .on(ElementSelector.<Constant>forType(CONSTANT)
                        .where(cp -> cp.getElement().getConstant().equals(1)))
                        .then(extension1)
                .on(ElementSelector.<Constant>forType(CONSTANT)
                        .where(cp -> cp.getElement().getConstant().equals(2)))
                        .then(extension2)
                .configuration();

        final CodePointerImpl codePointerWithConstant1 = new CodePointerImpl(method, constant(1));
        final CodePointerImpl codePointerWithConstant2 = new CodePointerImpl(method, constant(2));

        configuration.getDelegate(context, codePointerWithConstant1).apply(context, codePointerWithConstant1, out);

        verify(extension1).apply(eq(context), eq(codePointerWithConstant1), eq(out));
        verify(extension2, times(0)).apply(eq(context), eq(codePointerWithConstant1), eq(out));

        configuration.getDelegate(context, codePointerWithConstant2).apply(context, codePointerWithConstant2, out);

        verifyNoMoreInteractions(extension1);
        verify(extension2).apply(eq(context), eq(codePointerWithConstant2), eq(out));
    }

    @Test
    public void threeExtensionsCanBeConfiguredOnTehSameElementWithDifferentSelectors() {
        final CodeGeneratorConfiguration configuration = configurer
                .on(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(1)))).then(extension1)
                .on(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(2)))).then(extension2)
                .on(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(3)))).then(extension3)
                .configuration();

        final CodePointerImpl codePointer1 = new CodePointerImpl<>(method, constant(1));
        final CodePointerImpl codePointer2 = new CodePointerImpl<>(method, constant(2));
        final CodePointerImpl codePointer3 = new CodePointerImpl<>(method, constant(3));

        configuration.getDelegate(context, codePointer1).apply(context, codePointer1, out);
        configuration.getDelegate(context, codePointer2).apply(context, codePointer2, out);
        configuration.getDelegate(context, codePointer3).apply(context, codePointer3, out);

        final InOrder inOrder = Mockito.inOrder(extension1, extension2, extension3);

        inOrder.verify(extension1, times(1)).apply(eq(context), eq(codePointer1), eq(out));
        inOrder.verify(extension2, times(1)).apply(eq(context), eq(codePointer2), eq(out));
        inOrder.verify(extension3, times(1)).apply(eq(context), eq(codePointer3), eq(out));

        verifyNoMoreInteractions(extension1, extension2, extension3);
    }

    @Test
    public void singleAroundAdviceCanBeConfigured() {
        final CodeGeneratorAdvice expectedAdvice = mock(CodeGeneratorAdvice.class);

        final CodeGeneratorConfiguration configuration = configurer
                .around(ElementSelector.forType(CONSTANT)).then(expectedAdvice)
                .configuration();

        final CodePointer codePointer = new CodePointerImpl<>(method, constant(1));
        final CodeGeneratorAdvice<? extends Element> actualAdvice = configuration.getAdvice(context, codePointer);

        actualAdvice.apply(context, codePointer);

        verify(expectedAdvice).apply(eq(context), eq(codePointer));
    }

    @Test
    public void getAdviceShouldReturnNullIfNoAdviceExists() {
        expect(configurer.configuration().getAdvice(context, new CodePointerImpl<>(method, constant(1)))).toBe(equalTo(null));
    }

    @Test
    public void getAdviceShouldNotAcceptInvalidArguments() {
        final CodeGeneratorConfiguration configuration = configurer.configuration();

        expect(() -> configuration.getAdvice(null, mock(CodePointer.class))).toThrow(AssertionError.class);
        expect(() -> configuration.getAdvice(context, null)).toThrow(AssertionError.class);
    }

}
