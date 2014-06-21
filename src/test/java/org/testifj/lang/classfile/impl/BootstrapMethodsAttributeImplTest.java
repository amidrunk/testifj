package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.BootstrapMethod;
import org.testifj.lang.classfile.BootstrapMethodsAttribute;
import org.testifj.lang.classfile.impl.BootstrapMethodsAttributeImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class BootstrapMethodsAttributeImplTest {

    @Test
    public void constructorShouldNotAcceptNullMethods() {
        expect(() -> new BootstrapMethodsAttributeImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainBootstrapMethods() {
        final BootstrapMethod bootstrapMethod = mock(BootstrapMethod.class);
        final List<BootstrapMethod> methods = Arrays.asList(bootstrapMethod);
        final BootstrapMethodsAttribute attribute = new BootstrapMethodsAttributeImpl(methods);

        expect(attribute.getBootstrapMethods().toArray()).toBe(new Object[]{bootstrapMethod});
    }

}
