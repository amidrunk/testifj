package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ReturnImplTest {

    @Test
    public void returnValueShouldHaveCorrectElementType() {
        expect(new ReturnImpl().getElementType()).toBe(ElementType.RETURN);
    }

    @Test
    public void returnWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = Mockito.mock(ElementMetaData.class);

        expect(new ReturnImpl().getMetaData()).not().toBe(equalTo(null));
        expect(new ReturnImpl(metaData).getMetaData()).toBe(metaData);
    }

}
