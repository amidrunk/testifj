package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.impl.BootstrapMethodImpl;

import static org.testifj.Expect.expect;

public class BootstrapMethodImplTest {

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new BootstrapMethodImpl(-1, new int[]{0})).toThrow(AssertionError.class);
        expect(() -> new BootstrapMethodImpl(0, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final BootstrapMethodImpl method = new BootstrapMethodImpl(1, new int[]{2, 3});

        expect(method.getBootstrapMethodRef()).toBe(1);
        expect(method.getBootstrapArguments()).toBe(new int[]{2, 3});
    }

}
