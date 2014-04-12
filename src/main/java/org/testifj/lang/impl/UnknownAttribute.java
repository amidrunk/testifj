package org.testifj.lang.impl;

import org.testifj.lang.Attribute;

import java.util.Arrays;

public final class UnknownAttribute implements Attribute {

    private final String name;

    private final byte[] data;

    public UnknownAttribute(String name, byte[] data) {
        assert name != null : "name can't be null";
        assert data != null : "data can't be null";

        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof UnknownAttribute)) {
            return false;
        }

        final UnknownAttribute other = (UnknownAttribute) obj;

        return name.equals(other.name) && Arrays.equals(data, other.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{name.hashCode(), Arrays.hashCode(data)});
    }

    @Override
    public String toString() {
        return "UnknownAttribute{name=\"" + name + "\", data=" + Arrays.toString(data) + "}";
    }
}
