package org.testifj.lang.impl;

import org.testifj.lang.Lambda;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Signature;

import java.lang.reflect.Type;

public class LambdaImpl implements Lambda {

    private final Type functionalInterface;

    private final String functionalMethodName;

    private final Signature interfaceMethodSignature;

    private final Type declaringClass;

    private final String backingMethodName;

    private final Signature backingMethodSignature;

    public LambdaImpl(Type functionalInterface, String functionalMethodName, Signature interfaceMethodSignature, Type declaringClass, String backingMethodName, Signature backingMethodSignature) {
        this.functionalInterface = functionalInterface;
        this.functionalMethodName = functionalMethodName;
        this.interfaceMethodSignature = interfaceMethodSignature;
        this.declaringClass = declaringClass;
        this.backingMethodName = backingMethodName;
        this.backingMethodSignature = backingMethodSignature;
    }

    @Override
    public Type getFunctionalInterface() {
        return functionalInterface;
    }

    @Override
    public String getFunctionalMethodName() {
        return functionalMethodName;
    }

    @Override
    public Signature getInterfaceMethodSignature() {
        return interfaceMethodSignature;
    }

    @Override
    public Type getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public String getBackingMethodName() {
        return backingMethodName;
    }

    @Override
    public Signature getBackingMethodSignature() {
        return backingMethodSignature;
    }

    @Override
    public Type getType() {
        return functionalInterface; // TODO Generate generic type
    }

    @Override
    public ElementType getElementType() {
        return ElementType.LAMBDA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LambdaImpl lambda = (LambdaImpl) o;

        if (!backingMethodName.equals(lambda.backingMethodName)) return false;
        if (!backingMethodSignature.equals(lambda.backingMethodSignature)) return false;
        if (!declaringClass.equals(lambda.declaringClass)) return false;
        if (!functionalInterface.equals(lambda.functionalInterface)) return false;
        if (!functionalMethodName.equals(lambda.functionalMethodName)) return false;
        if (!interfaceMethodSignature.equals(lambda.interfaceMethodSignature)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = functionalInterface.hashCode();
        result = 31 * result + functionalMethodName.hashCode();
        result = 31 * result + interfaceMethodSignature.hashCode();
        result = 31 * result + declaringClass.hashCode();
        result = 31 * result + backingMethodName.hashCode();
        result = 31 * result + backingMethodSignature.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LambdaImpl{" +
                "functionalInterface=" + functionalInterface +
                ", functionalMethodName='" + functionalMethodName + '\'' +
                ", interfaceMethodSignature=" + interfaceMethodSignature +
                ", declaringClass=" + declaringClass +
                ", backingMethodName='" + backingMethodName + '\'' +
                ", backingMethodSignature=" + backingMethodSignature +
                '}';
    }
}
