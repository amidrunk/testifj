package org.testifj.lang.model;

import java.lang.reflect.Type;

public interface VariableAssignment extends Statement, Expression {

    Expression getValue();

    String getVariableName();

    Type getVariableType();

    default ElementType getElementType() {
        return ElementType.VARIABLE_ASSIGNMENT;
    }

}
