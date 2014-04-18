package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;

import static org.testifj.Expect.expect;

public class ReturnImplTest {

    @Test
    public void returnValueShouldHaveCorrectElementType() {
        expect(new ReturnImpl().getElementType()).toBe(ElementType.RETURN);
    }

}
