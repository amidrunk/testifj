package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementMetaData;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;

public class BasicModelFactoryTest {

    @Test
    public void createElementMetaDataShouldReturnEmptyMetaData() {
        final BasicModelFactory factory = new BasicModelFactory();
        final ElementMetaData metaData = factory.createElementMetaData();

        expect(metaData.hasLineNumber()).toBe(false);
        expect(metaData.hasProgramCounter()).toBe(false);
    }

}