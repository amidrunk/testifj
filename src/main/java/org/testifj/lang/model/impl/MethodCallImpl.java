package org.testifj.lang.model.impl;

import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;
import org.testifj.lang.model.Signature;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public final class MethodCallImpl implements MethodCall {

    private final Type targetType;

    private final String methodName;

    private final Signature signature;

    private final Expression targetInstance;

    private final Expression[] parameters;

    private final Type expressionType;

    public MethodCallImpl(Type targetType, String methodName, Signature signature, Expression targetInstance, Expression[] parameters) {
        this(targetType, methodName, signature, targetInstance, parameters, signature == null ? null : signature.getReturnType());
    }

    public MethodCallImpl(Type targetType, String methodName, Signature signature, Expression targetInstance, Expression[] parameters, Type expressionType) {
        assert targetType != null : "Target type can't be null";
        assert methodName != null && !methodName.isEmpty() : "Method name can't be null or empty";
        assert signature != null : "Signature can't be null";
        assert parameters != null : "Parameters can't be null";
        assert expressionType != null : "Expression type can't be null";

        this.targetType = targetType;
        this.methodName = methodName;
        this.signature = signature;
        this.targetInstance = targetInstance;
        this.parameters = parameters;
        this.expressionType = expressionType;
    }

    @Override
    public Type getTargetType() {
        return targetType;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public Expression getTargetInstance() {
        return targetInstance;
    }

    @Override
    public List<Expression> getParameters() {
        return Arrays.asList(parameters);
    }

    @Override
    public Type getType() {
        return expressionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodCallImpl that = (MethodCallImpl) o;

        if (!methodName.equals(that.methodName)) return false;
        if (!Arrays.equals(parameters, that.parameters)) return false;
        if (!signature.equals(that.signature)) return false;
        if (!targetInstance.equals(that.targetInstance)) return false;
        if (!targetType.equals(that.targetType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetType.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + signature.hashCode();
        result = 31 * result + targetInstance.hashCode();
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        return "MethodCallImpl{" +
                "targetType=" + targetType +
                ", methodName='" + methodName + '\'' +
                ", signature=" + signature +
                ", targetInstance=" + targetInstance +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
