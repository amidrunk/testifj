package org.testifj.lang.model;

public interface Constant extends Expression {

    Object getConstant();

    default ElementType getElementType() {
        return ElementType.CONSTANT;
    }

}
