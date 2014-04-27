package org.testifj.lang.model;

public interface Cast extends Expression {

    Expression getValue();

    default ElementType getElementType() {
        return ElementType.CAST;
    }

}
