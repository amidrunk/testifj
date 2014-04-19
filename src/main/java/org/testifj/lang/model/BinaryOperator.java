package org.testifj.lang.model;

public interface BinaryOperator extends Expression {

    Expression getLeftOperand();

    OperatorType getOperatorType();

    Expression getRightOperand();

    default ElementType getElementType() {
        return ElementType.BINARY_OPERATOR;
    }

}
