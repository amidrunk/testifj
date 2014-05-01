package org.testifj.lang.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.Caller;
import org.testifj.lang.ClassModelTestUtils;
import org.testifj.Procedure;
import org.testifj.lang.CodePointer;
import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.impl.DecompilationHistoryCallback.DecompilerState;
import static org.testifj.lang.model.AST.*;
import static org.testifj.lang.model.AST.eq;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.ObjectThatIs.instanceOf;
import static org.testifj.matchers.core.StringShould.containString;

public class DecompilerImplTest {

    private final Method exampleMethod = mock(Method.class);
    private final ClassFile exampleClassFile = mock(ClassFile.class);

    @Before
    public void setup() {
        when(exampleMethod.getClassFile()).thenReturn(exampleClassFile);
        when(exampleMethod.getLineNumberTable()).thenReturn(Optional.<LineNumberTable>empty());
    }

    @Test
    public void constructorShouldNotAcceptNullDecompilerConfiguration() {
        expect(() -> new DecompilerImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configuredDecompilerEnhancementShouldBeCalledAfterInstruction() throws IOException {
        final DecompilerEnhancement enhancement = mock(DecompilerEnhancement.class);
        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .enhance(ByteCode.istore_1, enhancement)
                .build();

        final DecompilerImpl decompiler = new DecompilerImpl(configuration);

        when(exampleMethod.getLocalVariableForIndex(eq(1))).thenReturn(new LocalVariableImpl(-1, -1, "test", String.class, 1));

        decompiler.parse(exampleMethod, new InputStreamCodeStream(new ByteArrayInputStream(new byte[]{(byte) ByteCode.iconst_0, ByteCode.istore_1})));

        verify(enhancement).enhance(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.istore_1));
    }

    @Test
    public void compilerExtensionCanOverrideByteCodeHandling() throws IOException {
        final DecompilerExtension extension = mock(DecompilerExtension.class);
        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .extend(ByteCode.iconst_0, extension)
                .build();
        final DecompilerImpl decompiler = new DecompilerImpl(configuration);

        when(extension.decompile(any(DecompilationContext.class), any(CodeStream.class), anyInt())).thenReturn(true);

        final Element[] elements = decompiler.parse(exampleMethod, new InputStreamCodeStream(new ByteArrayInputStream(new byte[]{(byte) ByteCode.iconst_0})));

        expect(elements.length).toBe(0);

        verify(extension).decompile(any(DecompilationContext.class), any(CodeStream.class), anyInt());
    }

    @Test
    public void decompilationProgressCallbackShouldBeNotifiedOfProgress() throws IOException {
        int n = 100;

        final Caller caller = Caller.adjacent(-2);
        final DecompilationHistoryCallback callback = new DecompilationHistoryCallback();

        decompileCallerWithCallback(caller, callback);

        expect(callback.getDecompilerStates()).toBe(new DecompilerState[]{
                new DecompilerState(Arrays.asList(AST.constant(100)), Collections.emptyList()),
                new DecompilerState(Collections.emptyList(), Arrays.asList(AST.set("n", int.class, AST.constant(100))))
        });
    }

    @Test
    public void decompilationCanBeAborted() throws IOException {
        int n = 100; int m = 200;

        final Caller caller = Caller.adjacent(-2);
        final DecompilationProgressCallback callback = mock(DecompilationProgressCallback.class);
        final DecompilationHistoryCallback history = new DecompilationHistoryCallback();

        doAnswer(i -> {
            final DecompilationContext context = (DecompilationContext) i.getArguments()[0];

            if (context.getStackedExpressions().isEmpty()) {
                context.abort();
            }

            return null;
        }).when(callback).onDecompilationProgressed(any());

        decompileCallerWithCallback(caller, new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{callback, history}));

        expect(history.getDecompilerStates()).toBe(new DecompilerState[]{
                new DecompilerState(Arrays.asList(AST.constant(100)), Collections.emptyList()),
                new DecompilerState(Collections.emptyList(), Arrays.asList(AST.set("n", int.class, AST.constant(100))))
        });
    }

    @Test
    public void emptyMethodCanBeParsed() {
        expect(parseMethodBody("emptyMethod")).toBe(new Element[]{$return()});
    }

    @Test
    public void methodWithReturnStatementCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithIntegerReturn");

        expect(elements).toBe(new Element[]{
                $return(constant(1234))
        });
    }

    @Test
    public void methodWithReturnFromOtherMethod() {
        final Element[] elements = parseMethodBody("exampleMethodWithReturnFromOtherMethod");

        expect(elements).toBe(new Element[]{
                $return(plus(constant(1), call(local("this", ExampleClass.class, 0), "methodWithIntegerReturn", int.class)))
        });
    }

    @Test
    public void methodWithReturnFromOtherMethodWithParameters() {
        final Element[] elements = parseMethodBody("exampleMethodWithMethodCallWithParameters");

        expect(elements).toBe(new Element[]{
                $return(call(local("this", ExampleClass.class, 0), "add", int.class, constant(1), constant(2)))
        });
    }

    @Test
    public void methodWithReturnOfLocalCanBeParsed() {
        final Element[] elements = parseMethodBody("returnLocal");

        final Element[] expectedElements = {
                set("n", constant(100)),
                $return(local("n", int.class, 1))
        };

        expect(elements).toBe(expectedElements);
    }

    @Test
    public void expectationsCanBeParsed() {
        expect(true).toBe(true);
        expect(ClassModelTestUtils.lineToString(-1)).toBe("expect(true).toBe(true)");
    }

    @Test
    public void methodWithReferencesToConstantsInConstantPoolCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithConstantPoolReferences");

        expect(elements).toBe(new Element[]{
                set("n", constant(123456789)),
                set("f", constant(123456789f)),
                set("str", constant("foobar")),
                $return()
        });
    }

    @Test
    public void methodWithFieldAccessCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithFieldAccess");

        final Element[] expectedElements = {
                call(field(local("this", ExampleClass.class, 0), String.class, "string"), "toString", String.class),
                $return()
        };

        expect(elements).toBe(expectedElements);
    }

    @Test
    public void methodWithLambdaDeclarationAndInvocationCanBeParsed() throws Exception {
        final Element[] elements = parseMethodBody("methodWithLambdaDeclarationAndInvocation");

        expect(elements.length).toBe(3);

        given((VariableAssignment) elements[0]).then(assignment -> {
            expect(assignment.getVariableName()).toBe("s");
            expect(assignment.getVariableType()).toBe(Supplier.class);
            expect(assignment.getValue()).toBe(instanceOf(Lambda.class));

            given((Lambda) assignment.getValue()).then(lambda -> {
                expect(lambda.getFunctionalInterface()).toBe(Supplier.class);
                expect(lambda.getFunctionalMethodName()).toBe("get");
                expect(lambda.getInterfaceMethodSignature()).toBe(MethodSignature.parse("()Ljava/lang/Object;"));
                expect(lambda.getBackingMethodSignature()).toBe(MethodSignature.parse("()Ljava/lang/String;"));
                expect(lambda.getDeclaringClass()).toBe(ExampleClass.class);
                expect(ExampleClass.class.getDeclaredMethod(lambda.getBackingMethodName())).not().toBe(equalTo(null));
                expect(lambda.getType()).toBe(Supplier.class);
            });
        });

        expect(elements[1]).toBe(call(local("s", Supplier.class, 1), "get", Object.class));
    }

    @Test
    public void methodWithStaticFieldReferenceCanBeParsed() {
        final Element[] actualElements = parseMethodBody("methodWithStaticFieldReference");
        final Element[] expectedElements = {
                set("b", field(BigDecimal.class, BigDecimal.class, "ZERO")),
                $return()
        };

        expect(actualElements).toBe(expectedElements);
    }

    @Test
    public void methodWithLongConstantsCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithLongConstants");

        expect(elements).toBe(new Element[]{
                set("l1", constant(0L)),
                set("l2", constant(1L)),
                $return()
        });
    }

    @Test
    public void methodWithByteConstantsCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithByteConstants");

        expect(elements).toBe(new Element[]{
                set("b1", byte.class, constant(0)),
                set("b2", byte.class, constant(1)),
                set("b3", byte.class, constant(2)),
                $return()
        });
    }

    @Test
    public void methodWithEqComparisonCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithEqComparison");

        expect(elements).toBe(new Element[]{
                set("b1", eq(call(constant("str"), "length", int.class), constant(3))),
                $return()
        });
    }

    @Test
    public void constantsOfAllTypesCanBeParsed() {
        final Element[] elements = parseMethodBody("constantsOfAllTypes");

        expect(elements).toBe(new Element[]{
                set("z", boolean.class, constant(1)),
                set("b", byte.class, constant(100)),
                set("s", short.class, constant(200)),
                set("c", char.class, constant(300)),
                set("n", int.class, constant(400)),
                set("l", long.class, constant(500L)),
                set("f", float.class, constant(600.1234f)),
                set("d", double.class, constant(700.1234d)),
                $return()
        });
    }

    @Test
    public void lambdaWithMethodCallThatDiscardsResultCanBeParsed() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder().create();

        expect(() -> constantPool.getInterfaceMethodRefDescriptor(1)).toThrow(IndexOutOfBoundsException.class);

        final CodePointer[] codePointers = ClassModelTestUtils.codeForLineOffset(-2);

        expect(codePointers.length).toBe(1);
        expect(ClassModelTestUtils.toCode(codePointers[0])).to(containString("expect(() -> constantPool.getInterfaceMethodRefDescriptor(1))"));
    }

    @Test
    public void newStatementCanBeDecompiled() {
        new String("Hello World!");

        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-2)).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{ newInstance(String.class, constant("Hello World!")) });
    }

    @Test
    public void newStatementWithAssignmentCanBeDecompiled() {
        final String str = new String("Hello World!");

        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-2)).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{ set("str", newInstance(String.class, constant("Hello World!"))) });
    }

    @Test
    public void newArrayWithAssignmentCanBeDecompiled() {
        final String[] array = {"Hello!"};

        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-2)).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new VariableAssignmentImpl(
                        new NewArrayImpl(String[].class, String.class, constant(1),
                                Arrays.asList(new ArrayInitializerImpl(0, constant("Hello!")))),
                        "array", String[].class)
        });
    }

    @Test
    public void arrayStoreCanBeDecompiled() {
        final String[] array = new String[1];

        array[0] = "Hello World!";

        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-2)).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new ArrayStoreImpl(local("array", String[].class, 1), constant(0), constant("Hello World!"))
        });
    }

    private String str = "astring";

    @Test
    public void fieldAssignmentCanBeDecompiled() {
        this.str = "newvalue";

        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-2)).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new FieldAssignmentImpl(
                        new FieldReferenceImpl(local("this", getClass(), 0), getClass(), String.class, "str"),
                        constant("newvalue"))
        });
    }

    @Test
    public void staticFieldReferenceCanBeDecompiled() {
        final PrintStream out = System.out;

        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-2)).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new VariableAssignmentImpl(
                        new FieldReferenceImpl(null, System.class, PrintStream.class, "out"),
                        "out", PrintStream.class)
        });
    }

    @Test
    public void staticFieldAssignmentCanBeDecompiled() {
        ExampleClass.STATIC_STRING = "bar";

        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-2)).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new FieldAssignmentImpl(
                        new FieldReferenceImpl(null, ExampleClass.class, String.class, "STATIC_STRING"),
                        constant("bar"))
        });
    }

    @Test
    public void typeCastCanBeDecompiled() {
        final Object object = "foo";
        final String string = (String) object;

        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-2)).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                AST.set("string", cast(local("object", Object.class, 1)).to(String.class))
        });
    }

    @Test
    public void lineNumbersShouldBeRetained() {
        final String string = "str";

        final Caller caller = Caller.adjacent(-2);
        final Element[] elements = Arrays.stream(ClassModelTestUtils.codeForLineOffset(-3)).map(CodePointer::getElement).toArray(Element[]::new);

        final VariableAssignment variableAssignments = (VariableAssignment) elements[0];
        expect(variableAssignments.getMetaData().getAttribute(ElementMetaData.LINE_NUMBER)).toBe(caller.getCallerStackTraceElement().getLineNumber());

        final Constant value = (Constant) variableAssignments.getValue();
        expect(value.getMetaData().getAttribute(ElementMetaData.LINE_NUMBER)).toBe(caller.getCallerStackTraceElement().getLineNumber());
    }

    private Element[] parseMethodBody(String methodName) {
        return ClassModelTestUtils.methodBodyOf(ExampleClass.class, methodName);
    }

    private void decompileCallerWithCallback(Caller caller, DecompilationProgressCallback callback) throws IOException {
        final Decompiler decompiler = new DecompilerImpl();
        final Method method = ClassModelTestUtils.methodWithName(getClass(), caller.getCallerStackTraceElement().getMethodName());

        try (CodeStream code = new InputStreamCodeStream(method.getCodeForLineNumber(caller.getCallerStackTraceElement().getLineNumber()))) {
            decompiler.parse(method, code, callback);
        }
    }

    private static void accept(Procedure procedure) {
    }

    private static class ExampleClass {

        public static String STATIC_STRING = "foo";

        private String string = new String("Hello World!");

        private void constantsOfAllTypes() {
            boolean z = true;
            byte b = 100;
            short s = 200;
            char c = 300;
            int n = 400;
            long l = 500;
            float f = 600.1234f;
            double d = 700.1234d;
        }

        private void methodWithLongConstants() {
            long l1 = 0;
            long l2 = 1;
        }

        private void methodWithByteConstants() {
            byte b1 = 0;
            byte b2 = 1;
            byte b3 = 2;
        }

        private void methodWithStaticFieldReference() {
            final BigDecimal b = BigDecimal.ZERO;
        }

        private void methodWithLambdaDeclarationAndInvocation() {
            final Supplier<String> s = () -> "Hello World!";
            s.get();
        }

        private void methodWithFieldAccess() {
            string.toString();
        }

        private void emptyMethod() {
        }

        private int methodWithIntegerReturn() {
            return 1234;
        }

        private int exampleMethodWithReturnFromOtherMethod() {
            return 1 + methodWithIntegerReturn();
        }

        private int add(int a, int b) {
            return a + b;
        }

        private int exampleMethodWithMethodCallWithParameters() {
            return add(1, 2);
        }

        private int returnLocal() {
            int n = 100;

            return n;
        }

        private void methodWithConstantPoolReferences() {
            int n = 123456789;
            float f = 123456789f;
            String str = "foobar";
        }

        private void methodWithEqComparison() {
            boolean b1 = "str".length() == 3;
        }

    }
}
