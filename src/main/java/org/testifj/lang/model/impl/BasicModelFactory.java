package org.testifj.lang.model.impl;

import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.EmptyElementMetaData;

public final class BasicModelFactory extends AbstractModelFactory {
    @Override
    protected ElementMetaData createElementMetaData() {
        return EmptyElementMetaData.EMPTY;
    }
}
