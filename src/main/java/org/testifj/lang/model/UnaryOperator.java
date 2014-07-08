package org.testifj.lang.model;

public interface UnaryOperator extends Expression {

    Expression getOperand();

    OperatorType getOperatorType();

}
