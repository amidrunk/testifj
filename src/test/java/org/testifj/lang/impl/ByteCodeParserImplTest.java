package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.CodePointer;
import org.testifj.Description;
import org.testifj.MethodElementDescriber;
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
import java.util.stream.StreamSupport;

import static org.junit.Assert.fail;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;
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
                new ReturnValueImpl(
                        new BinaryOperatorImpl(
                                new ConstantExpressionImpl(1, int.class),
                                OperatorType.PLUS,
                                new MethodCallImpl(
                                        getClass(),
                                        "methodWithIntegerReturn",
                                        SignatureImpl.parse("()I"),
                                        new LocalVariableReferenceImpl("this", getClass()),
                                        new Expression[0]),
                                int.class))
        });
    }

    @Test
    public void methodWithReturnFromOtherMethodWithParameters() {
        final Element[] elements = parseMethodBody("exampleMethodWithMethodCallWithParameters");

        expect(elements).toBe(new Element[]{
                new ReturnValueImpl(
                        new MethodCallImpl(
                                getClass(),
                                "add",
                                SignatureImpl.parse("(II)I"),
                                new LocalVariableReferenceImpl("this", getClass()),
                                new Expression[]{new ConstantExpressionImpl(1, int.class), new ConstantExpressionImpl(2, int.class)}))
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
        int lineNumber = -1;

        try {
            expect(true).toBe(false);
            fail();
        } catch (AssertionError e) {
            lineNumber = e.getStackTrace()[2].getLineNumber();
        }

        final CodePointer[] elements = parseLine(lineNumber);
        expect(elements.length).toBe(1);

        final Description codeDescription = new MethodElementDescriber().describe(elements[0]);
        expect(codeDescription.toString()).toBe("expect(true).toBe(false)");
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
                new MethodCallImpl(Object.class, "toString", SignatureImpl.parse("()Ljava/lang/String;"),
                        new FieldReferenceImpl(
                                new LocalVariableReferenceImpl("this", ByteCodeParserImplTest.class),
                                getClass(), ByteCodeParser.class, "parser"), new Expression[0]
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
        expect(lambda.getDeclaringClass()).toBe(getClass());
        expect(getClass().getDeclaredMethod(lambda.getBackingMethodName())).not().toBe(equalTo(null));
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

    private void methodWithStaticFieldReference() {
        final BigDecimal b = BigDecimal.ZERO;
    }

    private void methodWithLambdaDeclarationAndInvocation() {
        final Supplier<String> s = () -> "Hello World!";
        s.get();
    }

    private void methodWithFieldAccess() {
        parser.toString();
    }

    private CodePointer[] parseLine(int lineNumber) {
        final Method method = getMethod("expectationsCanBeParsed");

        try (InputStream in = method.getCodeForLineNumber(lineNumber)) {
            final Element[] elements = parser.parse(method, in);

            return Arrays.stream(elements).map(e -> new CodePointer(method, e)).toArray(CodePointer[]::new);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private Element[] parseMethodBody(String methodName) {
        final Method method = getMethod(methodName);

        try {
            return new ByteCodeParserImpl().parse(method, method.getCode().getCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Method getMethod(String name) {
        final ClassFileReaderImpl classFileReader = new ClassFileReaderImpl();

        try (InputStream in = getClass().getResourceAsStream("/" + getClass().getName().replace('.', '/') + ".class")) {
            final ClassFile classFile = classFileReader.read(in);

            return classFile.getMethods().stream().filter(m -> m.getName().equals(name)).findFirst().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
