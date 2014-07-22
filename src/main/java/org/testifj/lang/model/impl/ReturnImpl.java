package org.testifj.lang.model.impl;

import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.Return;

public final class ReturnImpl extends AbstractElement implements Return {

    public ReturnImpl() {
        this(null);
    }

    public ReturnImpl(ElementMetaData elementMetaData) {
        super(elementMetaData);
    }

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
