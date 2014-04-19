package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;

import static org.testifj.Expect.expect;

public class ConstantImplTest {

    @Test
    public void constructorShouldNotAcceptNullConstant() {
        expect(() -> new ConstantImpl(null, int.class)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullType() {
        expect(() -> new ConstantImpl(1234, null)).toThrow(AssertionError.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void constructorShouldCreateValidInstance() {
        final ConstantImpl constant = new ConstantImpl("foobar", String.class);

        expect(constant.getConstant()).toBe("foobar");
        expect(constant.getType()).toBe((Class) String.class);
        expect(constant.getElementType()).toBe(ElementType.CONSTANT);
    }

}
