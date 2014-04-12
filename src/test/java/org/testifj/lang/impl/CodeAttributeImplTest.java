package org.testifj.lang.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class CodeAttributeImplTest {
    
    @Test
    public void constructorShouldNotAcceptNullByteCode() {
        expect(() -> new CodeAttributeImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainByteCode() {
        final CodeAttributeImpl code = new CodeAttributeImpl(new byte[]{1, 2, 3, 4});

        expect(code.getData()).to(equal(new byte[]{1, 2, 3, 4}));
    }
    
}
