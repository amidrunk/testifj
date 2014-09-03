package org.testifj.lang.codegeneration.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testifj.BasicDescription;
import org.testifj.Description;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.SimpleCodeGeneratorConfiguration;
import org.testifj.lang.codegeneration.*;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.decompile.Decompiler;
import org.testifj.lang.decompile.impl.CodePointerImpl;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.ArrayInitializer;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.impl.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;
import static org.testifj.Caller.adjacent;
import static org.testifj.Expect.expect;
import static org.testifj.lang.ClassModelTestUtils.code;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.AST.local;

@SuppressWarnings("unchecked")
public class CodePointerCodeGeneratorTest {

    private final CodePointerCodeGenerator describer = new CodePointerCodeGenerator();

    private final Method method = mock(Method.class);
    private final Decompiler decompiler = mock(Decompiler.class);
    private final CodeGeneratorDelegate exampleDelegate = mock(CodeGeneratorDelegate.class);
    private final CodeGeneratorAdvice exampleAdvice = mock(CodeGeneratorAdvice.class);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new CodePointerCodeGenerator(null, mock(CodeGeneratorConfiguration.class))).toThrow(AssertionError.class);
        expect(() -> new CodePointerCodeGenerator(mock(Decompiler.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void aroundAdviceShouldBeCalledForMatchingElement() {
        final CodePointerCodeGenerator codeGenerator = new CodePointerCodeGenerator(decompiler, SimpleCodeGeneratorConfiguration.configurer()
                .on(ElementSelector.forType(ElementType.CONSTANT)).then(exampleDelegate)
                .around(ElementSelector.forType(ElementType.CONSTANT)).then(exampleAdvice)
                .configuration());

        doAnswer(answer -> {
            ((CodeGeneratorPointcut) answer.getArguments()[2])
                    .proceed((CodeGenerationContext) answer.getArguments()[0], (CodePointer) answer.getArguments()[1]);
            return null;
        }).when(exampleAdvice).apply(any(), any(), any());

        final CodePointer codePointer = pointer(constant(1));

        generatedCode(codeGenerator, codePointer);

        final InOrder inOrder = inOrder(exampleDelegate, exampleAdvice);

        inOrder.verify(exampleAdvice).apply(any(), Mockito.eq(codePointer), any());
        inOrder.verify(exampleDelegate).apply(any(), Mockito.eq(codePointer), any());
    }

    private String generatedCode(CodePointerCodeGenerator codeGenerator, CodePointer codePointer) {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(bout);

        codeGenerator.generateCode(codePointer, writer);

        writer.flush();

        return bout.toString();
    }

    // TODO: move these to some integration test catageory

    @Test
    public void returnCanBeDescribed() {
        final Description description = describer.describe(pointer(new ReturnImpl()));

        expect(description).toBe(BasicDescription.from("return"));
    }

    @Test
    public void lambdaMethodReferenceCanBeDescribed() {
        expect(this::lambdaTargetTest);

        final Description description = describer.describe(code(adjacent(-2))[0]);

        expect(description.toString()).toBe("expect(this::lambdaTargetTest)");
    }

    @Test
    public void lambdaWithImplicitThisAndOtherCodeCanBeDescribed() {
        expect(() -> {
            Math.random();
            this.lambdaTargetTest();
        });

        final Description description = describer.describe(code(adjacent(-5))[0]);

        expect(description.toString()).toBe(
            "expect(() -> {\n" +
            "   Math.random();\n" +
            "   lambdaTargetTest();\n" +
            ")"
        );
    }

    @Test
    public void lambdaWithInstanceMethodReferenceCanBeDescribed() {
        callFunction(String::length, "foo");

        final Description description = describer.describe(code(adjacent(-2))[0]);

        expect(description.toString()).toBe("callFunction(String::length, \"foo\")");
    }

    @Test
    public void lambdaWithArgumentsAndCodeCanBeDescribed() {
        callFunction(s -> s.length() + 1, "foo");

        final Description description = describer.describe(code(adjacent(-2))[0]);

        expect(description.toString()).toBe("callFunction(s -> s.length() + 1, \"foo\")");
    }

    @Test
    public void enumReferenceCanBeDescribed() {
        final ElementType fieldReference = ElementType.FIELD_REFERENCE;

        final Description description = describer.describe(code(adjacent(-2))[0]);

        expect(description.toString()).toBe("ElementType fieldReference = ElementType.FIELD_REFERENCE");
    }

    @Test
    @Ignore("Generics are hard")
    public void lambdaWithGenericsTypeParametersCanBeDescribed() {
        final Supplier<String> supplier = () -> "Hello World!";

        final Description description = describer.describe(code(adjacent(-2))[0]);

        expect(description.toString()).toBe("Supplier<String> supplier = () -> \"Hello World!\"");
    }

    @Test
    public void newInstanceCanBeDescribed() {
        final Description description = describer.describe(new CodePointerImpl(mock(Method.class), AST.newInstance(String.class, constant(1234), constant("foo"))));

        expect(description.toString()).toBe("new String(1234, \"foo\")");
    }

    @Test
    public void memberAccessOfPrivateVariableInInnerClassCanBeDescribed() {
        final ExampleInnerClass exampleInnerClass = new ExampleInnerClass();

        expect(exampleInnerClass.exampleVariable).toBe(0);

        final Description description = describer.describe(code(adjacent(-2))[0]);

        expect(description.toString()).toBe("expect(exampleInnerClass.exampleVariable).toBe(0)");
    }

    @Test
    public void arrayStoreCanBeDescribed(){
        final String code = toString(new ArrayStoreImpl(local("foo", String[].class, 1), constant(1234), constant("Hello World!")));

        expect(code).toBe("foo[1234] = \"Hello World!\"");
    }

    @Test
    public void newArrayWithoutInitializationCanBeDescribed() {
        final String code = toString(new NewArrayImpl(String[].class, String.class, constant(1), Collections.<ArrayInitializer>emptyList()));

        expect(code).toBe("new String[1]");
    }

    @Test
    public void newArrayWithInitializationCanBeDescribed() {
        final String code = toString(new NewArrayImpl(String[].class, String.class, constant(2), Arrays.asList(
                new ArrayInitializerImpl(0, constant("foo")),
                new ArrayInitializerImpl(1, constant("bar")))));

        expect(code).toBe("new String[] { \"foo\", \"bar\" }");
    }

    @Test
    public void innerClassFieldAssignmentCanBeDescribed() {
        final ExampleInnerClass exampleInnerClass = new ExampleInnerClass();

        exampleInnerClass.exampleVariable = 1234;

        final Description description = describer.describe(code(adjacent(-2))[0]);

        expect(description.toString()).toBe("exampleInnerClass.exampleVariable = 1234");
    }

    @Test
    public void typeCastCanBeDescribed() {
        final Description description = describer.describe(pointer(AST.cast(constant("foo")).to(String.class)));

        expect(description.toString()).toBe("(String)\"foo\"");
    }

    @Test
    public void arrayLoadCanBeDescribed() {
        final String generatedCode = toString(new ArrayLoadImpl(AST.local("foo", String[].class, 1), AST.constant(1234), String.class));

        expect(generatedCode).toBe("foo[1234]");
    }

    private String toString(Element element) {
        return describer.describe(new CodePointerImpl<>(method, element)).toString();
    }


    private CodePointer pointer(Element element) {
        return new CodePointerImpl(method, element);
    }

    private void lambdaTargetTest() {
    }

    private <T, R> R callFunction(Function<T, R> function, T arg) {
        return function.apply(arg);
    }

    private class ExampleInnerClass {

        private int exampleVariable;

    }

}
