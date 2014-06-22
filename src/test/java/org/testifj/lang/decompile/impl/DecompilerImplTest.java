package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testifj.Caller;
import org.testifj.lang.ClassModelTestUtils;
import org.testifj.Procedure;
import org.testifj.lang.classfile.*;
import org.testifj.lang.classfile.impl.DefaultConstantPool;
import org.testifj.lang.classfile.impl.LocalVariableImpl;
import org.testifj.lang.classfile.impl.LocalVariableTableImpl;
import org.testifj.lang.decompile.*;
import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import static org.testifj.Caller.adjacent;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.ClassModelTestUtils.code;
import static org.testifj.lang.ClassModelTestUtils.toCode;
import static org.testifj.lang.decompile.impl.DecompilationHistoryCallback.DecompilerState;
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
    public void advisoryDecompilerEnhancementShouldBeCalledBeforeInstructionIsProcessed() throws IOException {
        final DecompilerDelegate enhancement = mock(DecompilerDelegate.class);
        final DecompilerDelegate extension = mock(DecompilerDelegate.class);

        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .before(ByteCode.nop).then(enhancement)
                .on(ByteCode.nop).then(extension)
                .build();

        final DecompilerImpl decompiler = new DecompilerImpl(configuration);

        decompiler.parse(exampleMethod, CodeStreamTestUtils.codeStream(ByteCode.nop));

        final InOrder inOrder = Mockito.inOrder(extension, enhancement);

        inOrder.verify(enhancement).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.nop));
        inOrder.verify(extension).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.nop));
    }

    @Test
    public void correctionalDecompilerEnhancementShouldBeCalledAfterInstructionIsProcessed() throws IOException {
        final DecompilerDelegate extension = mock(DecompilerDelegate.class, "delegate");
        final DecompilerDelegate enhancement = mock(DecompilerDelegate.class, "enhancement");
        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .on(ByteCode.nop).then(extension)
                .after(ByteCode.nop).then(enhancement)
                .build();

        new DecompilerImpl(configuration).parse(exampleMethod, CodeStreamTestUtils.codeStream(ByteCode.nop));

        final InOrder inOrder = Mockito.inOrder(extension, enhancement);

        inOrder.verify(extension).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.nop));
        inOrder.verify(enhancement).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.nop));
    }

    @Test
    public void configuredDecompilerEnhancementShouldBeCalledAfterInstruction() throws IOException {
        final DecompilerDelegate enhancement = mock(DecompilerDelegate.class, "enhancement");
        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .after(ByteCode.istore_1).then(enhancement)
                .build();

        final DecompilerImpl decompiler = new DecompilerImpl(configuration.merge(CoreDecompilerDelegation.configuration()));

        when(exampleMethod.getLocalVariableTable()).thenReturn(Optional.of(new LocalVariableTableImpl(new LocalVariable[]{
                new LocalVariableImpl(-1, -1, "test", String.class, 1)
        })));

        decompiler.parse(exampleMethod, new InputStreamCodeStream(new ByteArrayInputStream(new byte[]{(byte) ByteCode.iconst_0, ByteCode.istore_1})));

        verify(enhancement).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.istore_1));
    }

    @Test
    public void compilerExtensionCanOverrideByteCodeHandling() throws IOException {
        final DecompilerDelegate extension = mock(DecompilerDelegate.class);
        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .on(ByteCode.iconst_0).then(extension)
                .build();
        final DecompilerImpl decompiler = new DecompilerImpl(configuration);

        final Element[] elements = decompiler.parse(exampleMethod, new InputStreamCodeStream(new ByteArrayInputStream(new byte[]{(byte) ByteCode.iconst_0})));

        expect(elements.length).toBe(0);

        verify(extension).apply(any(DecompilationContext.class), any(CodeStream.class), anyInt());
    }

    @Test
    public void decompilationProgressCallbackShouldBeNotifiedOfProgress() throws IOException {
        int n = 100;

        final Caller caller = adjacent(-2);
        final DecompilationHistoryCallback callback = new DecompilationHistoryCallback();

        decompileCallerWithCallback(caller, callback);

        expect(callback.getDecompilerStates()).toBe(new DecompilerState[]{
                new DecompilerState(Arrays.asList(AST.constant(100)), Collections.emptyList()),
                new DecompilerState(Collections.emptyList(), Arrays.asList(AST.set(1, "n", int.class, AST.constant(100))))
        });
    }

    @Test
    public void decompilationCanBeAborted() throws IOException {
        int n = 100; int m = 200;

        final Caller caller = adjacent(-2);
        final DecompilationProgressCallback callback = mock(DecompilationProgressCallback.class);
        final DecompilationHistoryCallback history = new DecompilationHistoryCallback();

        doAnswer(i -> {
            final DecompilationContext context = (DecompilationContext) i.getArguments()[0];

            if (context.getStackedExpressions().isEmpty()) {
                context.abort();
            }

            return null;
        }).when(callback).afterInstruction(any());

        decompileCallerWithCallback(caller, new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{callback, history}));

        expect(history.getDecompilerStates()).toBe(new DecompilerState[]{
                new DecompilerState(Arrays.asList(AST.constant(100)), Collections.emptyList()),
                new DecompilerState(Collections.emptyList(), Arrays.asList(AST.set(1, "n", int.class, AST.constant(100))))
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
                set(1, "n", constant(100)),
                $return(local("n", int.class, 1))
        };

        expect(elements).toBe(expectedElements);
    }

    @Test
    public void expectationsCanBeParsed() {
        expect(true).toBe(true);
        expect(toCode(code(adjacent(-1))[0])).toBe("expect(true).toBe(true)");
    }

    @Test
    public void methodWithReferencesToConstantsInConstantPoolCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithConstantPoolReferences");

        expect(elements).toBe(new Element[]{
                set(1, "n", constant(123456789)),
                set(2, "f", constant(123456789f)),
                set(3, "str", constant("foobar")),
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
                set(1, "b", field(BigDecimal.class, BigDecimal.class, "ZERO")),
                $return()
        };

        expect(actualElements).toBe(expectedElements);
    }

    @Test
    public void methodWithLongConstantsCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithLongConstants");

        expect(elements).toBe(new Element[]{
                set(1, "l1", constant(0L)),
                set(3, "l2", constant(1L)),
                $return()
        });
    }

    @Test
    public void methodWithByteConstantsCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithByteConstants");

        expect(elements).toBe(new Element[]{
                set(1, "b1", byte.class, constant(0)),
                set(2, "b2", byte.class, constant(1)),
                set(3, "b3", byte.class, constant(2)),
                $return()
        });
    }

    @Test
    public void methodWithEqComparisonCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithEqComparison");

        expect(elements).toBe(new Element[]{
                set(1, "b1", eq(call(constant("str"), "length", int.class), constant(3))),
                $return()
        });
    }

    @Test
    public void constantsOfAllTypesCanBeParsed() {
        final Element[] elements = parseMethodBody("constantsOfAllTypes");

        expect(elements).toBe(new Element[]{
                set(1, "z", boolean.class, constant(1)),
                set(2, "b", byte.class, constant(100)),
                set(3, "s", short.class, constant(200)),
                set(4, "c", char.class, constant(300)),
                set(5, "n", int.class, constant(400)),
                set(6, "l", long.class, constant(500L)),
                set(8, "f", float.class, constant(600.1234f)),
                set(9, "d", double.class, constant(700.1234d)),
                $return()
        });
    }

    @Test
    public void lambdaWithMethodCallThatDiscardsResultCanBeParsed() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder().create();

        expect(() -> constantPool.getInterfaceMethodRefDescriptor(1)).toThrow(IndexOutOfBoundsException.class);

        final CodePointer[] codePointers = code(adjacent(-2));

        expect(codePointers.length).toBe(1);
        expect(toCode(codePointers[0])).to(containString("expect(() -> constantPool.getInterfaceMethodRefDescriptor(1))"));
    }

    @Test
    public void newStatementCanBeDecompiled() {
        new String("Hello World!");

        final Element[] elements = Arrays.stream(code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{ newInstance(String.class, constant("Hello World!")) });
    }

    @Test
    public void newStatementWithAssignmentCanBeDecompiled() {
        final String str = new String("Hello World!");

        final Element[] elements = Arrays.stream(code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{ set(1, "str", newInstance(String.class, constant("Hello World!"))) });
    }

    @Test
    public void newArrayWithAssignmentCanBeDecompiled() {
        final String[] array = {"Hello!"};

        final Element[] elements = Arrays.stream(code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new VariableAssignmentImpl(
                        new NewArrayImpl(String[].class, String.class, constant(1),
                                Arrays.asList(new ArrayInitializerImpl(0, constant("Hello!")))),
                        1, "array", String[].class)
        });
    }

    @Test
    public void arrayStoreCanBeDecompiled() {
        final String[] array = new String[1];

        array[0] = "Hello World!";

        final Element[] elements = Arrays.stream(code(adjacent(-2)))
                .map(CodePointer::getElement)
                .toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new ArrayStoreImpl(local("array", String[].class, 1), constant(0), constant("Hello World!"))
        });
    }

    private String str = "astring";

    @Test
    public void fieldAssignmentCanBeDecompiled() {
        this.str = "newvalue";

        final Element[] elements = Arrays.stream(code(adjacent(-2)))
                .map(CodePointer::getElement)
                .toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new FieldAssignmentImpl(
                        new FieldReferenceImpl(local("this", getClass(), 0), getClass(), String.class, "str"),
                        constant("newvalue"))
        });
    }

    @Test
    public void staticFieldReferenceCanBeDecompiled() {
        final PrintStream out = System.out;

        final Element[] elements = Arrays.stream(code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                new VariableAssignmentImpl(
                        new FieldReferenceImpl(null, System.class, PrintStream.class, "out"),
                        1, "out", PrintStream.class)
        });
    }

    @Test
    public void staticFieldAssignmentCanBeDecompiled() {
        ExampleClass.STATIC_STRING = "bar";

        final Element[] elements = Arrays.stream(code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

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

        final Element[] elements = Arrays.stream(code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        expect(elements).toBe(new Element[]{
                AST.set(2, "string", cast(local("object", Object.class, 1)).to(String.class))
        });
    }

    @Test
    public void lineNumbersShouldBeRetained() {
        final String string = "str";

        final Caller caller = adjacent(-2);
        final Element[] elements = Arrays.stream(code(adjacent(-3))).map(CodePointer::getElement).toArray(Element[]::new);

        final VariableAssignment variableAssignments = (VariableAssignment) elements[0];
        expect(variableAssignments.getMetaData().getAttribute(ElementMetaData.LINE_NUMBER)).toBe(caller.getCallerStackTraceElement().getLineNumber());

        final Constant value = (Constant) variableAssignments.getValue();
        expect(value.getMetaData().getAttribute(ElementMetaData.LINE_NUMBER)).toBe(caller.getCallerStackTraceElement().getLineNumber());
    }

    @Test
    public void intArrayStoreCanBeDecompiled() {
        int[] array = new int[2];
        array[0] = 1234;

        final CodePointer codePointer = code(adjacent(-2))[0];

        expect(codePointer.getElement()).toBe(
                new ArrayStoreImpl(
                        new LocalVariableReferenceImpl("array", int[].class, 1),
                        AST.constant(0),
                        AST.constant(1234)));
    }

    @Test
    public void longArrayLoadCanBeDecompiled() {
        long[] array = new long[] {1};
        long l = array[0];

        expect(code(adjacent(-2))[0].getElement()).toBe(
                new VariableAssignmentImpl(
                        new ArrayLoadImpl(
                                new LocalVariableReferenceImpl("array", long[].class, 1),
                                AST.constant(0),
                                long.class),
                        2, "l", long.class));
    }

    @Test
    public void floatArrayLoadCanBeDecompiled() {
        float[] array = new float[]{1f};
        float f = array[0];

        expect(code(adjacent(-2))[0].getElement()).toBe(
                new VariableAssignmentImpl(
                        new ArrayLoadImpl(
                                new LocalVariableReferenceImpl("array", float[].class, 1),
                                AST.constant(0),
                                float.class),
                        2, "f", float.class));
    }

    @Test
    public void doubleArrayLoadCanBeDecompiled() {
        double[] array = new double[]{1d};
        double d = array[0];

        expect(code(adjacent(-2))[0].getElement()).toBe(
                new VariableAssignmentImpl(
                        new ArrayLoadImpl(
                                new LocalVariableReferenceImpl("array", double[].class, 1),
                                AST.constant(0),
                                double.class),
                        2, "d", double.class));
    }

    @Test
    public void booleanArrayLoadCanBeDecompiled() {
        boolean[] array = new boolean[]{true};
        boolean b = array[0];

        expect(code(adjacent(-2))[0].getElement()).toBe(
                new VariableAssignmentImpl(
                        new ArrayLoadImpl(
                                new LocalVariableReferenceImpl("array", boolean[].class, 1),
                                AST.constant(0),
                                boolean.class),
                        2, "b", boolean.class));
    }

    @Test
    public void charArrayLoadCanBeDecompiled() {
        char[] array = new char[]{'c'};
        char c = array[0];

        expect(code(adjacent(-2))[0].getElement()).toBe(
                new VariableAssignmentImpl(
                        new ArrayLoadImpl(
                                new LocalVariableReferenceImpl("array", char[].class, 1),
                                AST.constant(0),
                                char.class),
                        2, "c", char.class));
    }

    @Test
    public void shortArrayLoadCanBeDecompiled() {
        short[] array = new short[]{(short) 1};
        short s = array[0];

        expect(code(adjacent(-2))[0].getElement()).toBe(
                new VariableAssignmentImpl(
                        new ArrayLoadImpl(
                                new LocalVariableReferenceImpl("array", short[].class, 1),
                                AST.constant(0),
                                short.class),
                        2, "s", short.class));
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
