package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Signature;

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
    }

    @Test
    public void constructorShouldInitializeInstance() {
        final SignatureImpl signature = SignatureImpl.parse("()Ljava/lang/String;");
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

}
