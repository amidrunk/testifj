package org.testifj.lang.model.impl;

import org.testifj.lang.model.AllocateInstance;

import java.lang.reflect.Type;

public final class AllocateInstanceImpl implements AllocateInstance {

    private final Type type;

    public AllocateInstanceImpl(Type type) {
        assert type != null : "Type can't be null";
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AllocateInstanceImpl that = (AllocateInstanceImpl) o;

        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "AllocateInstanceImpl{" +
                "type=" + type +
                '}';
    }
}
