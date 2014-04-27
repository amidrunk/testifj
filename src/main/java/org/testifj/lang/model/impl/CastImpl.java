package org.testifj.lang.model.impl;

import org.testifj.lang.model.Cast;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;

import java.lang.reflect.Type;

public final class CastImpl extends AbstractElement implements Cast {

    private final Expression value;

    private final Type type;

    public CastImpl(Expression value, Type type) {
        assert value != null : "Value can't be null";
        assert type != null : "Type can't be null";

        this.value = value;
        this.type = type;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CastImpl cast = (CastImpl) o;

        if (!type.equals(cast.type)) return false;
        if (!value.equals(cast.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CastImpl{" +
                "value=" + value +
                ", type=" + type +
                '}';
    }
}
