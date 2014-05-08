package org.testifj.lang.model.impl;

import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Increment;

import java.lang.reflect.Type;

public class IncrementImpl extends AbstractElement implements Increment {

    private final Expression operand;

    public IncrementImpl(Expression operand) {
        assert operand != null : "Operand can't be null";

        this.operand = operand;
    }

    @Override
    public Expression getOperand() {
        return operand;
    }

    @Override
    public Type getType() {
        return operand.getType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IncrementImpl increment = (IncrementImpl) o;

        if (!operand.equals(increment.operand)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return operand.hashCode();
    }

    @Override
    public String toString() {
        return "IncrementImpl{" +
                "operand=" + operand +
                '}';
    }
}
