package org.testifj.lang.model;

import java.util.List;

public interface NewInstance extends Expression, Statement {

    Signature getConstructorSignature();

    List<Expression> getParameters();

    default ElementType getElementType() {
        return ElementType.NEW;
    }

}
