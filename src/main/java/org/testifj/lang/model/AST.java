package org.testifj.lang.model;

import org.testifj.lang.model.impl.ConstantExpressionImpl;

public final class AST {

    public static Expression constant(String constant) {
        assert constant != null : "String constant can't be null";
        return new ConstantExpressionImpl(constant, String.class);
    }

    public static Expression constant(int value) {
        return new ConstantExpressionImpl(value, int.class);
    }

}
