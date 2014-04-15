package org.testifj.lang;

import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Signature;

import java.lang.reflect.Type;

public interface Lambda extends Expression {

    Type getFunctionalInterface();

    String getFunctionalMethodName();

    Signature getInterfaceMethodSignature();

    Type getDeclaringClass();

    String getBackingMethodName();

    Signature getBackingMethodSignature();

}
