package org.testifj.lang.model.impl;

import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.EmptyElementMetaData;

public abstract class AbstractElement implements Element {

    private final ElementMetaData elementMetaData;

    protected AbstractElement() {
        this(null);
    }

    protected AbstractElement(ElementMetaData elementMetaData) {
        this.elementMetaData = (elementMetaData == null ? EmptyElementMetaData.EMPTY : elementMetaData);
    }

    @Override
    public ElementMetaData getMetaData() {
        return elementMetaData;
    }
}
