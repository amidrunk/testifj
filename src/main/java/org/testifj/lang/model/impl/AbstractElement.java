package org.testifj.lang.model.impl;

import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementMetaData;

public abstract class AbstractElement implements Element {

    private final ElementMetaData elementMetaData = new ElementMetaDataImpl();

    @Override
    public ElementMetaData getMetaData() {
        return elementMetaData;
    }
}
