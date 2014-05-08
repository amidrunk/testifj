package org.testifj.lang.model.impl;

import org.testifj.lang.model.ArrayStore;
import org.testifj.lang.model.Expression;

public class ArrayStoreImpl extends AbstractElement implements ArrayStore {

    private final Expression array;

    private final Expression index;

    private final Expression value;

    public ArrayStoreImpl(Expression array, Expression index, Expression value) {
        assert array != null : "Array can't be null";
        assert index != null : "Index can't be null";
        assert value != null : "Value can't be null";

        this.array = array;
        this.index = index;
        this.value = value;
    }

    @Override
    public Expression getArray() {
        return array;
    }

    @Override
    public Expression getIndex() {
        return index;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayStoreImpl that = (ArrayStoreImpl) o;

        if (!array.equals(that.array)) return false;
        if (!index.equals(that.index)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = array.hashCode();
        result = 31 * result + index.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ArrayStoreImpl{" +
                "array=" + array +
                ", index=" + index +
                ", value=" + value +
                '}';
    }
}