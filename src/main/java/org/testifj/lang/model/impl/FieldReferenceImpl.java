package org.testifj.lang.model.impl;

import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.FieldReference;

import java.lang.reflect.Type;

public final class FieldReferenceImpl implements FieldReference {

    private final Expression targetInstance;

    private final Type declaringType;

    private final Type fieldType;

    private final String fieldName;

    public FieldReferenceImpl(Expression targetInstance, Type declaringType, Type fieldType, String fieldName) {
        assert targetInstance != null : "Target instance can't be null";
        assert declaringType != null : "Declaring type can't be null";
        assert fieldType != null : "Field type can't be null";
        assert fieldName != null && !fieldName.isEmpty() : "Field name can't be null or empty";

        this.targetInstance = targetInstance;
        this.declaringType = declaringType;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
    }

    @Override
    public Expression getTargetInstance() {
        return targetInstance;
    }

    @Override
    public Type getDeclaringType() {
        return declaringType;
    }

    public Type getFieldType() {
        return fieldType;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Type getType() {
        return fieldType;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.FIELD_REFERENCE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldReferenceImpl that = (FieldReferenceImpl) o;

        if (!declaringType.equals(that.declaringType)) return false;
        if (!fieldName.equals(that.fieldName)) return false;
        if (!fieldType.equals(that.fieldType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = declaringType.hashCode();
        result = 31 * result + fieldType.hashCode();
        result = 31 * result + fieldName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldReferenceImpl{" +
                "declaringType=" + declaringType +
                ", fieldType=" + fieldType +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
