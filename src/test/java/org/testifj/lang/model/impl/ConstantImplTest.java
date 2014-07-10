package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ConstantImplTest {

    @Test
    public void constructorShouldNotAcceptNullConstantIfTypeIsPrimitive() {
        expect(() -> new ConstantImpl(null, int.class)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullType() {
        expect(() -> new ConstantImpl(1234, null)).toThrow(AssertionError.class);
    }

    @Test
    public void objectConstantCanBeNull() {
        given(new ConstantImpl(null, Object.class)).then(c -> {
            expect(c.getType()).toBe(Object.class);
            expect(c.getConstant()).toBe(equalTo(null));
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void constructorShouldCreateValidInstance() {
        final ConstantImpl constant = new ConstantImpl("foobar", String.class);

        expect(constant.getConstant()).toBe("foobar");
        expect(constant.getType()).toBe((Class) String.class);
        expect(constant.getElementType()).toBe(ElementType.CONSTANT);
    }

    @Test
    public void constantWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(new ConstantImpl("foo", String.class).getMetaData()).not().toBe(equalTo(null));
        expect(new ConstantImpl("foo", String.class, metaData).getMetaData()).toBe(metaData);
    }

}
