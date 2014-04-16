package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.ClassModelTestUtils;
import org.testifj.CodeDescriber;
import org.testifj.CodePointer;
import org.testifj.Description;
import org.testifj.lang.ByteCodeParser;
import org.testifj.lang.ClassFile;
import org.testifj.lang.Lambda;
import org.testifj.lang.Method;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;
import org.testifj.lang.model.VariableAssignment;
import org.testifj.lang.model.impl.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.junit.Assert.fail;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.ObjectThatIs.instanceOf;

public class ByteCodeParserImplTest {

    private final ByteCodeParser parser = new ByteCodeParserImpl();

    @Test
    public void emptyMethodCanBeParsed() {
        expect(parseMethodBody("emptyMethod")).toBe(new Element[]{new ReturnImpl()});
    }

    @Test
    public void methodWithReturnStatementCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithIntegerReturn");

        expect(elements).toBe(new Element[]{
                new ReturnValueImpl(new ConstantExpressionImpl(1234, int.class))
        });
    }

    @Test
    public void methodWithReturnFromOtherMethod() {
        final Element[] elements = parseMethodBody("exampleMethodWithReturnFromOtherMethod");

        expect(elements).toBe(new Element[]{
                new ReturnValueImpl(new BinaryOperatorImpl(
                        new ConstantExpressionImpl(1, int.class),
                        OperatorType.PLUS,
                        new MethodCallImpl(
                                ExampleClass.class,
                                "methodWithIntegerReturn",
                                SignatureImpl.parse("()I"),
                                new LocalVariableReferenceImpl("this", ExampleClass.class),
                                new Expression[0]),
                        int.class
                ))
        });
    }

    @Test
    public void methodWithReturnFromOtherMethodWithParameters() {
        final Element[] elements = parseMethodBody("exampleMethodWithMethodCallWithParameters");

        expect(elements).toBe(new Element[]{
                new ReturnValueImpl(
                        new MethodCallImpl(
                                ExampleClass.class,
                                "add",
                                SignatureImpl.parse("(II)I"),
                                new LocalVariableReferenceImpl("this", ExampleClass.class),
                                new Expression[]{new ConstantExpressionImpl(1, int.class), new ConstantExpressionImpl(2, int.class)})
                )
        });
    }

    @Test
    public void methodWithReturnOfLocalCanBeParsed() {
        final Element[] elements = parseMethodBody("returnLocal");

        final Element[] expectedElements = {
                new VariableAssignmentImpl(new ConstantExpressionImpl(100, int.class), "n", int.class),
                new ReturnValueImpl(new LocalVariableReferenceImpl("n", int.class))
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

        expect(elements.length).toBe(4);
        expect(elements[0]).toBe(new VariableAssignmentImpl(new ConstantExpressionImpl(123456789, int.class), "n", int.class));
        expect(elements[1]).toBe(new VariableAssignmentImpl(new ConstantExpressionImpl(123456789f, float.class), "f", float.class));
        expect(elements[2]).toBe(new VariableAssignmentImpl(new ConstantExpressionImpl("foobar", String.class), "str", String.class));
        expect(elements[3]).toBe(new ReturnImpl());
    }

    @Test
    public void methodWithFieldAccessCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithFieldAccess");

        final Element[] expectedElements = {
                new MethodCallImpl(String.class, "toString", SignatureImpl.parse("()Ljava/lang/String;"),
                        new FieldReferenceImpl(
                                new LocalVariableReferenceImpl("this", ExampleClass.class),
                                ExampleClass.class, String.class, "string"), new Expression[0]
                ),
                new ReturnImpl()
        };

        expect(elements).toBe(expectedElements);
    }

    @Test
    public void methodWithLambdaDeclarationAndInvocationCanBeParsed() throws Exception {
        final Element[] elements = parseMethodBody("methodWithLambdaDeclarationAndInvocation");

        expect(elements.length).toBe(3);

        final VariableAssignment assignment = (VariableAssignment) elements[0];
        expect(assignment.getVariableName()).toBe("s");
        expect(assignment.getVariableType()).toBe(Supplier.class);
        expect(assignment.getValue()).toBe(instanceOf(Lambda.class));

        final Lambda lambda = (Lambda) assignment.getValue();
        expect(lambda.getFunctionalInterface()).toBe(Supplier.class);
        expect(lambda.getFunctionalMethodName()).toBe("get");
        expect(lambda.getInterfaceMethodSignature()).toBe(SignatureImpl.parse("()Ljava/lang/Object;"));
        expect(lambda.getBackingMethodSignature()).toBe(SignatureImpl.parse("()Ljava/lang/String;"));
        expect(lambda.getDeclaringClass()).toBe(ExampleClass.class);
        expect(ExampleClass.class.getDeclaredMethod(lambda.getBackingMethodName())).not().toBe(equalTo(null));
        expect(lambda.getType()).toBe(Supplier.class);

        expect(elements[1]).toBe(new MethodCallImpl(
                Supplier.class,
                "get",
                SignatureImpl.parse("()Ljava/lang/Object;"),
                new LocalVariableReferenceImpl("s", Supplier.class),
                new Expression[0]
        ));
    }

    @Test
    public void methodWithStaticFieldReferenceCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithStaticFieldReference");

        expect(elements).toBe(new Element[]{
                new FieldReferenceImpl(null, BigDecimal.class, BigDecimal.class, "ZERO"),
                new ReturnImpl()
        });
    }

    @Test
    public void methodWithLongConstantsCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithLongConstants");

        expect(elements).toBe(new Element[]{
                new VariableAssignmentImpl(new ConstantExpressionImpl(0L, long.class), "l1", long.class),
                new VariableAssignmentImpl(new ConstantExpressionImpl(1L, long.class), "l2", long.class),
                new ReturnImpl()
        });
    }

    private Element[] parseMethodBody(String methodName) {
        return ClassModelTestUtils.methodBodyOf(ExampleClass.class, methodName);
    }

    private static class ExampleClass {

        private String string = new String("Hello World!");

        private void methodWithLongConstants() {
            long l1 = 0;
            long l2 = 1;
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

    }
}
