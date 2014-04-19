package org.testifj.lang.model;

public interface Branch extends Statement {

    Expression getLeftOperand();

    OperatorType getOperatorType();

    Expression getRightOperand();

    int getTargetPC();

    default ElementType getElementType() {
        return ElementType.BRANCH;
    }

}
