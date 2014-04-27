package org.testifj.lang.model.impl;

import org.testifj.lang.model.Constant;

import java.lang.reflect.Type;

public final class ConstantImpl extends AbstractElement implements Constant {

    private final Object constant;

    private final Class type;

    public ConstantImpl(Object constant, Class type) {
        assert type != null : "Type can't be null";
        assert !type.isPrimitive() || constant != null : "Constant can't be null";

        this.constant = constant;
        this.type = type;
    }

    @Override
    public Object getConstant() {
        return constant;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstantImpl that = (ConstantImpl) o;

        if (!constant.equals(that.constant)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return constant.hashCode();
    }

    @Override
    public String toString() {
        return "ConstantImpl{" +
                "constant=" + constant + ", " +
                "type=" + type +
                '}';
    }
}
