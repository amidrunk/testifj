package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementMetaData;

import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class DefaultModelFactoryTest {

    @Test
    public void constructorShouldNotAcceptNullMetaDataSupplier() {
        expect(() -> new DefaultModelFactory(null)).toThrow(AssertionError.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createElementMetaDataShouldReturnMetaDataFromSupplier() {
        final Supplier<ElementMetaData> supplier = mock(Supplier.class);
        final ElementMetaData metaData = mock(ElementMetaData.class);

        when(supplier.get()).thenReturn(metaData);

        given(new DefaultModelFactory(supplier)).then(modelFactory -> {
            expect(modelFactory.createElementMetaData()).toBe(metaData);
            verify(supplier).get();
        });
    }

}