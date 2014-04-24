package org.testifj.lang.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.Expect;
import org.testifj.ExpectValueContinuation;
import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.ArrayInitializerImpl;
import org.testifj.lang.model.impl.MethodSignature;
import org.testifj.lang.model.impl.NewArrayImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.AST.local;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

@SuppressWarnings("unchecked")
public class CoreCodeGenerationExtensionsTest {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PrintWriter out = new PrintWriter(baos);
    private final CodeGeneratorConfiguration configuration = coreConfiguration();

    private final CodeGenerationDelegate codeGenerationDelegate = (context, codePointer) -> {
        configuration.getExtension(context, codePointer).call(context, codePointer, out);
    };

    private final CodeStyle codeStyle = mock(CodeStyle.class);

    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(codeGenerationDelegate, codeStyle);

    private final Method method = mock(Method.class);

    @Before
    public void setup() {
        doAnswer(invocationOnMock -> ((Class) invocationOnMock.getArguments()[0]).getSimpleName())
                .when(codeStyle).getTypeName(any(Type.class));
        when(codeStyle.shouldOmitThis()).thenReturn(false);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> CoreCodeGenerationExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void coreConfigurationShouldSupportGenerationForReturnElement() {
        expect(codeFor(AST.$return())).toBe("return");
    }

    @Test
    public void coreConfigurationShouldSupportGenerationForConstantElements() {
        expect(codeFor(AST.constant(1.234d))).toBe("1.234");
        expect(codeFor(AST.constant(1.234f))).toBe("1.234f");
        expect(codeFor(AST.constant(1234L))).toBe("1234L");
        expect(codeFor(AST.constant(1234))).toBe("1234");
        expect(codeFor(AST.constant("foo"))).toBe("\"foo\"");
    }

    @Test
    public void coreConfigurationShouldSupportGenerationOfReturnValue() {
        expect(codeFor(AST.$return(constant(1)))).toBe("return 1");
    }

    @Test
    public void coreConfigurationShouldSupportGenerationOfVariableReference() {
        expect(codeFor(AST.local("foo", String.class, 1))).toBe("foo");
    }

    @Test
    public void coreConfigurationShouldSupportInstanceMethodCall() {
        expect(codeFor(AST.call(constant("foo"), "toString", String.class))).toBe("\"foo\".toString()");
        expect(codeFor(AST.call(constant("foo"), "length", int.class))).toBe("\"foo\".length()");
        expect(codeFor(AST.call(constant("foo"), "substring", String.class, constant(1), constant(2)))).toBe("\"foo\".substring(1, 2)");
    }

    @Test
    public void codeConfigurationShouldSupportStaticMethodCall() {
        final MethodCall staticMethodCall = AST.call(String.class, "valueOf", String.class, constant(1));

        expect(codeFor(staticMethodCall)).toBe("String.valueOf(1)");
    }

    @Test
    public void booleanBoxCallShouldBeSupported() {
        final MethodSignature valueOfSignature = MethodSignature.parse("(Z)Ljava/lang/Boolean;");
        final MethodCall trueCall = AST.call(Boolean.class, "valueOf", valueOfSignature, constant(1));
        final MethodCall falseCall = AST.call(Boolean.class, "valueOf", valueOfSignature, constant(0));

        expect(codeFor(trueCall)).toBe("true");
        expect(codeFor(falseCall)).toBe("false");
    }

    @Test
    public void selectBooleanBoxCallShouldSelectBooleanValueOfMethod() {
        final ElementSelector<MethodCall> selector = CoreCodeGenerationExtensions.selectBooleanBoxCall();
        final CodePointerImpl<MethodCall> codePointer = new CodePointerImpl<>(method,
                AST.call(Boolean.class, "valueOf", MethodSignature.parse("(Z)Ljava/lang/Boolean;"), constant(0)));

        expect(selector.getElementType()).toBe(ElementType.METHOD_CALL);
        expect(selector.matches(codePointer)).toBe(true);
    }

    @Test
    public void isDSLMethodCallShouldReturnTrueIfTargetTypeHasDSLAnnotation() {
        final boolean isDSLCall = CoreCodeGenerationExtensions.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(Expect.class, "expect", ExpectValueContinuation.class, local("foo", Object.class, 1))));

        expect(isDSLCall).toBe(true);
    }

    @Test
    public void isDSLMethodCallShouldReturnFalseIfTargetTypeDoesNotHaveDSLAnnotation() {
        final boolean isDSLCall = CoreCodeGenerationExtensions.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(String.class, "valueOf", String.class, constant("foo"))));

        expect(isDSLCall).toBe(false);
    }

    @Test
    public void isDSLMethodCallShouldReturnFalseIfTargetTypeIsNotAClass() {
        final boolean isDSLMethodCall = CoreCodeGenerationExtensions.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(mock(Type.class), "foo", String.class, constant(1))));

        expect(isDSLMethodCall).toBe(false);
    }

    @Test
    public void selectDSLCallShouldSelectMethodCallWhereTypeHasDSLAnnotation() {
        final boolean isDSLMethodCall = CoreCodeGenerationExtensions.selectDSLMethodCall().matches(new CodePointerImpl<>(
                method, AST.call(Expect.class, "expect", ExpectValueContinuation.class, local("foo", Object.class, 1))
        ));

        expect(isDSLMethodCall).toBe(true);
    }

    @Test
    public void dslMethodCallsShouldGenerateCallsWithoutTargetTypeSpecified() {
        final String code = codeFor(AST.call(Expect.class, "expect", ExpectValueContinuation.class, constant("foo")));

        expect(code).toBe("expect(\"foo\")");
    }

    @Test
    public void thisReferenceCanBeOmittedOnInstanceMethodCall() {
        when(codeStyle.shouldOmitThis()).thenReturn(true);

        final String code = codeFor(new CodePointerImpl(method, AST.call(local("this", Object.class, 0), "foo", String.class)));

        expect(code).toBe("foo()");
    }

    @Test
    public void thisReferenceCanBeIncludedOnInstanceMethodCall() {
        when(codeStyle.shouldOmitThis()).thenReturn(false);

        final String code = codeFor(new CodePointerImpl(method, AST.call(local("this", Object.class, 0), "foo", String.class)));

        expect(code).toBe("this.foo()");
    }

    @Test
    public void selectUninitializedNewArrayShouldSelectNewArrayWithoutInitializers() {
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Collections.emptyList()));

        given(codePointer).then(it -> {
            expect(CoreCodeGenerationExtensions.selectUninitializedNewArray().matches(codePointer)).toBe(true);
        });
    }

    @Test
    public void selectUninitializedNewArrayShouldNotSElectNewArrayWithInitializers(){
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Arrays.asList(new ArrayInitializerImpl(0, constant("foo")))));

        given(codePointer).then(it -> {
            expect(CoreCodeGenerationExtensions.selectUninitializedNewArray().matches(codePointer)).toBe(false);
        });
    }

    @Test
    public void selectInitializedNewArrayShouldSelectNewArrayWithInitializers() {
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Arrays.asList(new ArrayInitializerImpl(0, constant("foo")))));

        given(codePointer).then(it -> {
            expect(CoreCodeGenerationExtensions.selectInitializedNewArray().matches(codePointer)).toBe(true);
        });
    }

    @Test
    public void selectInitializedNewArrayShouldNotSelectNewArrayWithoutInitializers() {
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Collections.emptyList()));

        given(codePointer).then(it -> {
            expect(CoreCodeGenerationExtensions.selectInitializedNewArray().matches(codePointer)).toBe(false);
        });
    }

    @Test
    public void primitiveBoxCallsShouldBeHandledAndGeneratedAsImplicit() {
        expect(codeFor(new CodePointerImpl(method, AST.call(Byte.class, "valueOf",
                MethodSignature.parse("(B)Ljava/lang/Byte;"), constant(1))))).toBe("1");
        expect(codeFor(new CodePointerImpl(method, AST.call(Short.class, "valueOf",
                MethodSignature.parse("(S)Ljava/lang/Short;"), constant(1))))).toBe("1");
        expect(codeFor(new CodePointerImpl(method, AST.call(Character.class, "valueOf",
                MethodSignature.parse("(C)Ljava/lang/Character;"), constant(1))))).toBe("1");
        expect(codeFor(new CodePointerImpl(method, AST.call(Integer.class, "valueOf",
                MethodSignature.parse("(I)Ljava/lang/Integer;"), constant(1))))).toBe("1");
        expect(codeFor(new CodePointerImpl(method, AST.call(Long.class, "valueOf",
                MethodSignature.parse("(J)Ljava/lang/Long;"), constant(1L))))).toBe("1L");
        expect(codeFor(new CodePointerImpl(method, AST.call(Float.class, "valueOf",
                MethodSignature.parse("(F)Ljava/lang/Float;"), constant(1F))))).toBe("1.0f");
        expect(codeFor(new CodePointerImpl(method, AST.call(Double.class, "valueOf",
                MethodSignature.parse("(D)Ljava/lang/Double;"), constant(1D))))).toBe("1.0");
    }

    @Test
    public void isPrimitiveBoxCallShouldReturnTrueForBoxMethods() {
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = CoreCodeGenerationExtensions.isPrimitiveBoxCall();

        expect(primitiveBoxCall.test(codePointer(AST.call(Byte.class, "valueOf",
                MethodSignature.parse("(B)Ljava/lang/Byte;"), constant(1))))).toBe(true);
        expect(primitiveBoxCall.test(codePointer(AST.call(Short.class, "valueOf",
                MethodSignature.parse("(S)Ljava/lang/Short;"), constant(1))))).toBe(true);
        expect(primitiveBoxCall.test(codePointer(AST.call(Character.class, "valueOf",
                MethodSignature.parse("(C)Ljava/lang/Character;"), constant(1))))).toBe(true);
        expect(primitiveBoxCall.test(codePointer(AST.call(Integer.class, "valueOf",
                MethodSignature.parse("(I)Ljava/lang/Integer;"), constant(1))))).toBe(true);
        expect(primitiveBoxCall.test(codePointer(AST.call(Long.class, "valueOf",
                MethodSignature.parse("(J)Ljava/lang/Long;"), constant(1L))))).toBe(true);
        expect(primitiveBoxCall.test(codePointer(AST.call(Float.class, "valueOf",
                MethodSignature.parse("(F)Ljava/lang/Float;"), constant(1F))))).toBe(true);
        expect(primitiveBoxCall.test(codePointer(AST.call(Double.class, "valueOf",
                MethodSignature.parse("(D)Ljava/lang/Double;"), constant(1D))))).toBe(true);
    }

    @Test
    public void isPrimitiveBoxCallShouldReturnFalseForInstanceMethod() {
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = CoreCodeGenerationExtensions.isPrimitiveBoxCall();

        expect(primitiveBoxCall.test(codePointer(AST.call(constant(1), "valueOf", Integer.class, constant(1))))).toBe(false);
    }

    @Test
    public void isPrimitiveBoxCallShouldReturnFalseForNonMatchingStaticMethod() {
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = CoreCodeGenerationExtensions.isPrimitiveBoxCall();

        expect(primitiveBoxCall.test(codePointer(AST.call(Integer.class, "valueOf", Integer.class, constant(1), constant(10))))).toBe(false);
    }

    private <T extends Element> CodePointer<T> codePointer(T element) {
        final CodePointer codePointer = mock(CodePointer.class);

        when(codePointer.getElement()).thenReturn(element);
        when(codePointer.getMethod()).thenThrow(new UnsupportedOperationException());

        return codePointer;
    }

    private String codeFor(Element element) {
        return codeFor(new CodePointerImpl(method, element));
    }

    private String codeFor(CodePointerImpl codePointer) {
        final CodeGeneratorExtension extension = coreConfiguration().getExtension(context, codePointer);

        expect(extension).not().toBe(equalTo(null));

        baos.reset();
        extension.call(context, codePointer, out);
        out.flush();

        return baos.toString();
    }

    private CodeGeneratorConfiguration coreConfiguration() {
        final SimpleCodeGeneratorConfiguration.Builder builder = new SimpleCodeGeneratorConfiguration.Builder();
        CoreCodeGenerationExtensions.configure(builder);

        return builder.build();
    }

}
