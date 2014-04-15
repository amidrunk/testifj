package org.testifj.lang.model;

import java.lang.reflect.Type;

public interface FieldReference extends Expression {

    Expression getTargetInstance();

    Type getDeclaringType();

    Type getFieldType();

    String getFieldName();

}
