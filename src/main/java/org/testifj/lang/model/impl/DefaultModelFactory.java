package org.testifj.lang.model.impl;

import org.testifj.lang.model.ElementMetaData;

import java.util.function.Supplier;

public final class DefaultModelFactory extends AbstractModelFactory {

    private final Supplier<ElementMetaData> elementMetaDataSupplier;

    public DefaultModelFactory(Supplier<ElementMetaData> elementMetaDataSupplier) {
        assert elementMetaDataSupplier != null : "Meta data supplier can't be null";
        this.elementMetaDataSupplier = elementMetaDataSupplier;
    }

    @Override
    protected ElementMetaData createElementMetaData() {
        return elementMetaDataSupplier.get();
    }
}
