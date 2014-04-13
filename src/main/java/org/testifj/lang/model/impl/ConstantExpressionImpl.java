package org.testifj.lang.model.impl;

import org.testifj.lang.model.ConstantExpression;
import org.testifj.lang.model.ElementType;

import java.lang.reflect.Type;

public final class ConstantExpressionImpl implements ConstantExpression {

    private final Object constant;

    public ConstantExpressionImpl(Object constant) {
        assert constant != null : "Constant can't be null";
        this.constant = constant;
    }

    @Override
    public Object getConstant() {
        return constant;
    }

    @Override
    public Type getType() {
        return constant.getClass();
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
                "constant=" + constant +
                '}';
    }
}
