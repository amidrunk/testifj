package org.testifj.lang.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.ClassFile;
import org.testifj.lang.Method;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;
import org.testifj.lang.model.impl.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class ByteCodeParserImplTest {

    @Test
    public void emptyMethodCanBeParsed() {
        expect(parseMethod("emptyMethod")).to(equal(new Element[]{new ReturnImpl()}));
    }

    @Test
    public void methodWithReturnStatementCanBeParsed() {
        final Element[] elements = parseMethod("methodWithIntegerReturn");

        expect(elements).to(equal(new Element[]{
                new ReturnValueImpl(new ConstantExpressionImpl(1234))
        }));
    }

    @Test
    public void methodWithReturnFromOtherMethod() {
        final Element[] elements = parseMethod("exampleMethodWithReturnFromOtherMethod");

        expect(elements).to(equal(new Element[]{
                new ReturnValueImpl(
                        new BinaryOperatorImpl(
                                new ConstantExpressionImpl(1),
                                OperatorType.PLUS,
                                new MethodCallImpl(
                                        getClass(),
                                        "methodWithIntegerReturn",
                                        SignatureImpl.parse("()I"),
                                        new LocalVariableReferenceImpl("this", getClass()),
                                        new Expression[0]),
                                int.class))
        }));
    }

    @Test
    public void methodWithReturnFromOtherMethodWithParameters() {
        final Element[] elements = parseMethod("exampleMethodWithMethodCallWithParameters");

        expect(elements).to(equal(new Element[]{
                new ReturnValueImpl(
                        new MethodCallImpl(
                                getClass(),
                                "add",
                                SignatureImpl.parse("(II)I"),
                                new LocalVariableReferenceImpl("this", getClass()),
                                new Expression[]{new ConstantExpressionImpl(1), new ConstantExpressionImpl(2)}))
        }));
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

    private Element[] parseMethod(String methodName) {
        final ClassFileReaderImpl classFileReader = new ClassFileReaderImpl();

        try (InputStream in = getClass().getResourceAsStream("/" + getClass().getName().replace('.', '/') + ".class")) {
            final ClassFile classFile = classFileReader.read(in);
            final Method method = classFile.getMethods().stream().filter(m -> m.getName().equals(methodName)).findFirst().get();

            return new ByteCodeParserImpl().parse(classFile, method.getCode().getCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Element[] parse(int... byteCode) {
        final byte[] bytes = new byte[byteCode.length];

        for (int i = 0; i < byteCode.length; i++) {
            bytes[i] = (byte) byteCode[i];
        }

        try {
            return new ByteCodeParserImpl().parse(Mockito.mock(ClassFile.class), new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
