package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class ReturnValueImplTest {

    @Test
    public void constructorShouldNotAcceptNullReturnValue() {
        expect(() -> new ReturnValueImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeReturnValue() {
        final Expression expression = mock(Expression.class);
        final ReturnValueImpl returnValue = new ReturnValueImpl(expression);

        expect(returnValue.getValue()).to(equal(expression));
        expect(returnValue.getElementType()).to(equal(ElementType.RETURN_VALUE));
    }

}
