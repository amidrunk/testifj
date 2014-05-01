package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.*;

import java.lang.reflect.Type;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class MethodCallImplTest {

    private final Signature exampleSignature = mock(Signature.class);
    private final Type exampleType = mock(Type.class);
    private final Expression exampleInstance = mock(Expression.class);

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new MethodCallImpl(null, "foo", exampleSignature, exampleInstance, new Expression[0])).toThrow(AssertionError.class);
        expect(() -> new MethodCallImpl(exampleType, null, exampleSignature, exampleInstance, new Expression[0])).toThrow(AssertionError.class);
        expect(() -> new MethodCallImpl(exampleType, "", exampleSignature, exampleInstance, new Expression[0])).toThrow(AssertionError.class);
        expect(() -> new MethodCallImpl(exampleType, "foo", null, exampleInstance, new Expression[0])).toThrow(AssertionError.class);
        expect(() -> new MethodCallImpl(exampleType, "foo", exampleSignature, exampleInstance, null)).toThrow(AssertionError.class);
        expect(() -> new MethodCallImpl(exampleType, "foo", exampleSignature, exampleInstance, new Expression[0], null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        final MethodSignature signature = MethodSignature.parse("()Ljava/lang/String;");
        final Expression parameter = mock(Expression.class);
        final MethodCallImpl methodCall = new MethodCallImpl(exampleType, "foo", signature, exampleInstance, new Expression[]{parameter});

        expect(methodCall.getElementType()).toBe(ElementType.METHOD_CALL);
        expect(methodCall.getMethodName()).toBe("foo");
        expect(methodCall.getSignature()).toBe(signature);
        expect(methodCall.getType()).toBe(signature.getReturnType());
        expect(methodCall.getTargetType()).toBe(exampleType);
        expect(methodCall.getTargetInstance()).toBe(exampleInstance);
        expect(methodCall.getParameters().toArray()).toBe(new Object[]{parameter});
    }

    @Test
    public void typeOfExpressionCanBeSpecifiedExplicitly() {
        final MethodCallImpl methodCall = new MethodCallImpl(String.class, "toString",
                MethodSignature.parse("()V"), new ConstantImpl("foo", String.class), new Expression[0], int.class);

        expect(methodCall.getType()).toBe(int.class);
    }

    @Test
    public void isStaticShouldBeTrueForStaticMethodCall() {
        final MethodCall methodCall = AST.call(String.class, "valueOf", String.class);

        expect(methodCall.isStatic()).toBe(true);
    }

    @Test
    public void isStaticCallShouldBeFalseForInstanceMethodCall() {
        final MethodCall methodCall = AST.call(AST.constant("foo"), "toString", String.class);

        expect(methodCall.isStatic()).toBe(false);
    }
}
