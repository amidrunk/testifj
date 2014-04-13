package org.testifj.lang.model.impl;

import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.ReturnValue;

public final class ReturnValueImpl implements ReturnValue {

    private final Expression expression;

    public ReturnValueImpl(Expression expression) {
        assert expression != null : "Expression can't be null";
        this.expression = expression;
    }

    @Override
    public Expression getValue() {
        return expression;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.RETURN_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnValueImpl that = (ReturnValueImpl) o;

        if (!expression.equals(that.expression)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }

    @Override
    public String toString() {
        return "ReturnValueImpl{" +
                "expression=" + expression +
                '}';
    }
}
