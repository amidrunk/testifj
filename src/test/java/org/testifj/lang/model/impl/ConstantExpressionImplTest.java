package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class ConstantExpressionImplTest {

    @Test
    public void constructorShouldNotAcceptNullConstant() {
        expect(() -> new ConstantExpressionImpl(null, int.class)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullType() {
        expect(() -> new ConstantExpressionImpl(1234, null)).toThrow(AssertionError.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void constructorShouldCreateValidInstance() {
        final ConstantExpressionImpl constant = new ConstantExpressionImpl("foobar", String.class);

        expect(constant.getConstant()).to(equal("foobar"));
        expect(constant.getType()).to(equal((Class) String.class));
        expect(constant.getElementType()).to(equal(ElementType.CONSTANT));
    }

}
