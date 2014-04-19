package org.testifj.lang.model.impl;

import org.testifj.lang.model.Return;

public final class ReturnImpl implements Return {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ReturnImpl)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ReturnImpl{}";
    }
}
