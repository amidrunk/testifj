package org.testifj.lang.model.impl;

import org.testifj.lang.model.Expression;
import org.testifj.lang.model.VariableAssignment;

import java.lang.reflect.Type;

public final class VariableAssignmentImpl implements VariableAssignment {

    private final Expression value;

    private final String variableName;

    private final Type variableType;

    public VariableAssignmentImpl(Expression value, String variableName, Type variableType) {
        assert value != null : "Value can't be null";
        assert variableName != null && !variableName.isEmpty() : "Variable name can't be null or empty";
        assert variableType != null : "Variable type can't be null";

        this.value = value;
        this.variableName = variableName;
        this.variableType = variableType;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public Type getVariableType() {
        return variableType;
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableAssignmentImpl that = (VariableAssignmentImpl) o;

        if (!value.equals(that.value)) return false;
        if (!variableName.equals(that.variableName)) return false;
        if (!variableType.equals(that.variableType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + variableName.hashCode();
        result = 31 * result + variableType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "VariableAssignmentImpl{" +
                "value=" + value +
                ", variableName='" + variableName + '\'' +
                ", variableType=" + variableType +
                '}';
    }
}
