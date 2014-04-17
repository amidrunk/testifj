package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;

import static org.testifj.Expect.expect;

public class JumpImplTest {

    @Test
    public void targetPCCannotBeNegative() {
        expect(() -> new JumpImpl(-1)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        final JumpImpl jump = new JumpImpl(1234);

        expect(jump.getTargetPC()).toBe(1234);
        expect(jump.getElementType()).toBe(ElementType.JUMP);
    }

}
