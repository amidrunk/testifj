package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

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

}
