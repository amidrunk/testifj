package org.testifj.lang.model.impl;

import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.LocalVariableReference;

import java.lang.reflect.Type;

public class LocalVariableReferenceImpl implements LocalVariableReference {

    private final String variableName;

    private final Type variableType;

    private final int index;

    public LocalVariableReferenceImpl(String variableName, Type variableType, int index) {
        assert variableName != null && !variableName.isEmpty() : "Variable name can't be null or empty";
        assert variableType != null : "Variable type can't be null";
        assert index >= 0 : "Index must be positive";

        this.variableName = variableName;
        this.variableType = variableType;
        this.index = index;
    }

    @Override
    public String getName() {
        return variableName;
    }

    @Override
    public Type getType() {
        return variableType;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.VARIABLE_REFERENCE;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalVariableReferenceImpl that = (LocalVariableReferenceImpl) o;

        if (!variableName.equals(that.variableName)) return false;
        if (!variableType.equals(that.variableType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = variableName.hashCode();
        result = 31 * result + variableType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LocalVariableReferenceImpl{" +
                "variableName='" + variableName + '\'' +
                ", variableType=" + variableType +
                '}';
    }
}
