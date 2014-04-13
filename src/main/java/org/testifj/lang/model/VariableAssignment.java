package org.testifj.lang.model;

import org.testifj.lang.LocalVariable;

import java.lang.reflect.Type;

public interface VariableAssignment extends Statement, Expression {

    Expression getValue();

    String getVariableName();

    Type getVariableType();

}
