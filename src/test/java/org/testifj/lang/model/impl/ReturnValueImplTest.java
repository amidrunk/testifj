package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ReturnValueImplTest {

    @Test
    public void constructorShouldNotAcceptNullReturnValue() {
        expect(() -> new ReturnValueImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeReturnValue() {
        final Expression expression = mock(Expression.class);
        final ReturnValueImpl returnValue = new ReturnValueImpl(expression);

        expect(returnValue.getValue()).toBe(expression);
        expect(returnValue.getElementType()).toBe(ElementType.RETURN_VALUE);
    }

    @Test
    public void returnValueWithMetaDataCanBeCreated() {
        final Expression value = mock(Expression.class);
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(new ReturnValueImpl(value, metaData)).not().toBe(equalTo(null));
        expect(new ReturnValueImpl(value, metaData).getMetaData()).toBe(metaData);
    }

}
