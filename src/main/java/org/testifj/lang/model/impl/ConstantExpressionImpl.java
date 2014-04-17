package org.testifj.lang.model.impl;

import org.testifj.lang.model.ConstantExpression;
import org.testifj.lang.model.ElementType;

import java.lang.reflect.Type;

public final class ConstantExpressionImpl implements ConstantExpression {

    private final Object constant;

    private final Class type;

    public ConstantExpressionImpl(Object constant, Class type) {
        assert constant != null : "Constant can't be null";
        assert type != null : "Type can't be null";

        this.constant = constant;
        this.type = type;
    }

    @Override
    public Object getConstant() {
        return constant;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.CONSTANT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstantExpressionImpl that = (ConstantExpressionImpl) o;

        if (!constant.equals(that.constant)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return constant.hashCode();
    }

    @Override
    public String toString() {
        return "ConstantExpressionImpl{" +
                "constant=" + constant + ", " +
                "type=" + type +
                '}';
    }
}
