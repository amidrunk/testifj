package org.testifj.lang.codegeneration.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.Expect;
import org.testifj.ExpectValueContinuation;
import org.testifj.lang.classfile.ClassPathClassFileResolver;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.ClassFileReaderImpl;
import org.testifj.lang.classfile.impl.SimpleCodeGeneratorConfiguration;
import org.testifj.lang.classfile.impl.SimpleTypeResolver;
import org.testifj.lang.codegeneration.*;
import org.testifj.lang.decompile.*;
import org.testifj.lang.decompile.impl.CodePointerImpl;
import org.testifj.lang.decompile.impl.DecompilerImpl;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.codegeneration.impl.JavaSyntaxCodeGeneration.selectInnerClassFieldAccess;
import static org.testifj.lang.model.AST.*;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

@SuppressWarnings("unchecked")
public class JavaSyntaxCodeGenerationTest {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PrintWriter out = new PrintWriter(baos);
    private final CodeGeneratorConfiguration configuration = coreConfiguration();

    private final CodeGenerationDelegate codeGenerationDelegate = (context, codePointer) -> {
        configuration.getDelegate(context, codePointer).apply(context, codePointer, out);
    };

    private final CodeStyle codeStyle = mock(CodeStyle.class);

    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(
            codeGenerationDelegate,
            new SimpleTypeResolver(),
            new ClassPathClassFileResolver(new ClassFileReaderImpl()),
            new DecompilerImpl(),
            codeStyle);

    private final Method method = mock(Method.class);

    private final JavaSyntaxCodeGeneration delegation = new JavaSyntaxCodeGeneration();

    @Before
    public void setup() {
        doAnswer(invocationOnMock -> ((Class) invocationOnMock.getArguments()[0]).getSimpleName())
                .when(codeStyle).getTypeName(any(Type.class));
        when(codeStyle.shouldOmitThis()).thenReturn(false);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> delegation.configure(null)).toThrow(AssertionError.class);
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
        final ElementSelector<MethodCall> selector = JavaSyntaxCodeGeneration.selectBooleanBoxCall();
        final CodePointerImpl<MethodCall> codePointer = new CodePointerImpl<>(method,
                AST.call(Boolean.class, "valueOf", MethodSignature.parse("(Z)Ljava/lang/Boolean;"), constant(0)));

        expect(selector.getElementType()).toBe(ElementType.METHOD_CALL);
        expect(selector.matches(codePointer)).toBe(true);
    }

    @Test
    public void isDSLMethodCallShouldReturnTrueIfTargetTypeHasDSLAnnotation() {
        final boolean isDSLCall = JavaSyntaxCodeGeneration.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(Expect.class, "expect", ExpectValueContinuation.class, local("foo", Object.class, 1))));

        expect(isDSLCall).toBe(true);
    }

    @Test
    public void isDSLMethodCallShouldReturnFalseIfTargetTypeDoesNotHaveDSLAnnotation() {
        final boolean isDSLCall = JavaSyntaxCodeGeneration.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(String.class, "valueOf", String.class, constant("foo"))));

        expect(isDSLCall).toBe(false);
    }

    @Test
    public void isDSLMethodCallShouldReturnFalseIfTargetTypeIsNotAClass() {
        final boolean isDSLMethodCall = JavaSyntaxCodeGeneration.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(mock(Type.class), "foo", String.class, constant(1))));

        expect(isDSLMethodCall).toBe(false);
    }

    @Test
    public void selectDSLCallShouldSelectMethodCallWhereTypeHasDSLAnnotation() {
        final boolean isDSLMethodCall = JavaSyntaxCodeGeneration.selectDSLMethodCall().matches(new CodePointerImpl<>(
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
            expect(JavaSyntaxCodeGeneration.selectUninitializedNewArray().matches(codePointer)).toBe(true);
        });
    }

    @Test
    public void selectUninitializedNewArrayShouldNotSElectNewArrayWithInitializers(){
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Arrays.asList(new ArrayInitializerImpl(0, constant("foo")))));

        given(codePointer).then(it -> {
            expect(JavaSyntaxCodeGeneration.selectUninitializedNewArray().matches(codePointer)).toBe(false);
        });
    }

    @Test
    public void selectInitializedNewArrayShouldSelectNewArrayWithInitializers() {
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Arrays.asList(new ArrayInitializerImpl(0, constant("foo")))));

        given(codePointer).then(it -> {
            expect(JavaSyntaxCodeGeneration.selectInitializedNewArray().matches(codePointer)).toBe(true);
        });
    }

    @Test
    public void selectInitializedNewArrayShouldNotSelectNewArrayWithoutInitializers() {
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Collections.emptyList()));

        given(codePointer).then(it -> {
            expect(JavaSyntaxCodeGeneration.selectInitializedNewArray().matches(codePointer)).toBe(false);
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
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = JavaSyntaxCodeGeneration.isPrimitiveBoxCall();

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
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = JavaSyntaxCodeGeneration.isPrimitiveBoxCall();

        expect(primitiveBoxCall.test(codePointer(AST.call(constant(1), "valueOf", Integer.class, constant(1))))).toBe(false);
    }

    @Test
    public void isPrimitiveBoxCallShouldReturnFalseForNonMatchingStaticMethod() {
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = JavaSyntaxCodeGeneration.isPrimitiveBoxCall();

        expect(primitiveBoxCall.test(codePointer(AST.call(Integer.class, "valueOf", Integer.class, constant(1), constant(10))))).toBe(false);
    }

    @Test
    public void selectInnerClassFieldAccessShouldNotSelectNonMethodCall() {
        expect(((ElementSelector) selectInnerClassFieldAccess()).matches(codePointer(AST.constant(1)))).toBe(false);
    }

    @Test
    public void selectInnerClassFieldAccessShouldNotSelectNonStaticMethodCall() {
        final boolean matchesInstanceMethod = selectInnerClassFieldAccess()
                .matches(codePointer(call(AST.constant("foo"), "toString", String.class)));

        expect(matchesInstanceMethod).toBe(false);
    }

    @Test
    public void innerClassFieldSelectorShouldNotSelectStaticMethodWithInvalidArguments() {
        expect(selectInnerClassFieldAccess().matches(codePointer(AST.call(String.class, "access$100", String.class, constant("foo"), constant("bar"), constant("baz"))))).toBe(false);
        expect(selectInnerClassFieldAccess().matches(codePointer(AST.call(String.class, "valueOf", String.class, constant("foo"))))).toBe(false);
        expect(selectInnerClassFieldAccess().matches(codePointer(AST.call(String.class, "access$100", String.class)))).toBe(false);
    }

    @Test
    public void innerClassFieldSelectorShouldNotSelectNonInnerClassMethodCall() {
        expect(selectInnerClassFieldAccess().matches(codePointer(AST.call(String.class, "access$100", String.class, constant(1234))))).toBe(false);
    }

    @Test
    public void innerClassFieldSelectorShouldSelectInnerClassFieldReference() {
        final boolean innerClassFieldReferenceSelected = selectInnerClassFieldAccess()
                .matches(codePointer(AST.call(Inner.class, "access$100", String.class, local("myInner", Inner.class, 1))));

        expect(innerClassFieldReferenceSelected).toBe(true);
    }

    @Test
    public void innerClassFieldAccessShouldGeneratePlainFieldReference() {
        final Inner inner = new Inner();
        final String str = inner.str;

        final MethodCall innerClassFieldAccessor = AST.call(Inner.class, "access$100", String.class, local("myInner", Inner.class, 1));

        expect(codeFor(innerClassFieldAccessor)).toBe("myInner.str");
    }

    @Test
    public void innerClassFieldAssignmentShouldGeneratePlainFieldAssignment() {
        final Inner inner = new Inner();

        inner.str = "foo";

        final MethodCall innerClassFieldAssignment = AST.call(Inner.class, "access$102", String.class, local("myInner", Inner.class, 1), constant("foo"));

        expect(codeFor(innerClassFieldAssignment)).toBe("myInner.str = \"foo\"");
    }

    @Test
    public void castExtensionShouldBeSupported() {
        expect(codeFor(AST.cast(constant("foo")).to(String.class))).toBe("(String)\"foo\"");
    }

    @Test
    public void classConstantShouldOutputClass() {
        expect(codeFor(constant(String.class))).toBe("String.class");
    }

    @Test
    public void arrayLoadShouldOutputArrayElementAccess() {
        final String code = codeFor(new ArrayLoadImpl(AST.local("foo", String[].class, 1), AST.constant(1234), String.class));

        expect(code).toBe("foo[1234]");
    }

    @Test
    public void allocateInstanceShouldBeSupported() {
        expect(codeFor(new InstanceAllocationImpl(String.class))).toBe("new String<uninitialized>");
    }

    @Test
    public void binaryOperatorsShouldBeSupported() {
        expect(codeFor(add(constant(1), constant(2), int.class))).toBe("1 + 2");
        expect(codeFor(sub(constant(1), constant(2), int.class))).toBe("1 - 2");
        expect(codeFor(mul(constant(1), constant(2), int.class))).toBe("1 * 2");
        expect(codeFor(div(constant(1), constant(2), int.class))).toBe("1 / 2");
        expect(codeFor(mod(constant(1), constant(2), int.class))).toBe("1 % 2");
        expect(codeFor(lshift(constant(1), constant(2), int.class))).toBe("1 << 2");
        expect(codeFor(rshift(constant(1), constant(2), int.class))).toBe("1 >> 2");
        expect(codeFor(unsignedRightShift(constant(1), constant(2), int.class))).toBe("1 >>> 2");
        expect(codeFor(eq(constant(1), constant(2)))).toBe("1 == 2");
        expect(codeFor(ne(constant(1), constant(2)))).toBe("1 != 2");
        expect(codeFor(lt(constant(1), constant(2)))).toBe("1 < 2");
        expect(codeFor(le(constant(1), constant(2)))).toBe("1 <= 2");
        expect(codeFor(gt(constant(1), constant(2)))).toBe("1 > 2");
        expect(codeFor(ge(constant(1), constant(2)))).toBe("1 >= 2");
        expect(codeFor(and(constant(1), constant(2)))).toBe("1 && 2");
        expect(codeFor(or(constant(1), constant(2)))).toBe("1 || 2");
        expect(codeFor(bitwiseAnd(constant(1), constant(2), int.class))).toBe("1 & 2");
        expect(codeFor(bitwiseOr(constant(1), constant(2), int.class))).toBe("1 | 2");
        expect(codeFor(xor(constant(1), constant(2), int.class))).toBe("1 ^ 2");
    }

    @Test
    public void varArgsWithNoOtherArgumentsShouldBeSupportedInStatic() {
        final String code = codeFor(call(Varargs.class, "varArgs1", void.class,
                AST.newArray(String[].class, constant("foo"), constant("bar"), constant("baz"))));

        assertEquals("Varargs.varArgs1(\"foo\", \"bar\", \"baz\")", code);
    }

    @Test
    public void varArgsWithNoOtherArgumentsShouldBeSupportedInStatic() {
        final String code = codeFor(call(Varargs.class, "varArgs1", void.class,
                AST.newArray(String[].class, constant("foo"), constant("bar"), constant("baz"))));

        assertEquals("Varargs.varArgs1(\"foo\", \"bar\", \"baz\")", code);
    }

    // Support classes
    //

    private static class Inner {

        private String str;

    }

    private static class Varargs {

        public void varArgs1(String ... args) {
        }

        public void varArgs2(int head, int ... tail) {
        }

    }

    // Support methods
    //

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
        final CodeGeneratorDelegate extension = coreConfiguration().getDelegate(context, codePointer);

        expect(extension).not().toBe(equalTo(null));

        baos.reset();
        extension.apply(context, codePointer, out);
        out.flush();

        return baos.toString();
    }

    private CodeGeneratorConfiguration coreConfiguration() {
        final CodeGeneratorConfigurer configurer = SimpleCodeGeneratorConfiguration.configurer();
        new JavaSyntaxCodeGeneration().configure(configurer);
        return configurer.configuration();
    }

}
