package org.testifj.lang.model;

public interface Increment extends Expression, Statement {

    Expression getOperand();

    default ElementType getElementType() {
        return ElementType.INCREMENT;
    }

}
