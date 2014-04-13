package org.testifj.lang.model;

import java.lang.reflect.Type;
import java.util.List;

public interface MethodCall extends Expression, Statement {

    Type getTargetType();

    String getMethodName();

    Signature getSignature();

    Expression getTargetInstance();

    List<Expression> getParameters();

}
