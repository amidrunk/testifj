package org.testifj.lang;

import org.testifj.lang.model.Expression;
import org.testifj.lang.model.LocalVariableReference;
import org.testifj.lang.model.Signature;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public interface Lambda extends Expression {

    ReferenceKind getReferenceKind();

    Optional<Expression> getSelf();

    Type getFunctionalInterface();

    String getFunctionalMethodName();

    Signature getInterfaceMethodSignature();

    Type getDeclaringClass();

    String getBackingMethodName();

    Signature getBackingMethodSignature();

    List<LocalVariableReference> getEnclosedVariables();

}
