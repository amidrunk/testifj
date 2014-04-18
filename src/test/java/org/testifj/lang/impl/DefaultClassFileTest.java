package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;

public class DefaultClassFileTest {

    private final ConstantPool constantPool = mock(ConstantPool.class);

    @Test
    public void bootstrapMethodAttributesCanBeResolvedIfExists() {
        final BootstrapMethodsAttribute attribute = mock(BootstrapMethodsAttribute.class);
        final ClassFile classFile = DefaultClassFile.fromVersion(0, 0)
                .withConstantPool(constantPool)
                .withSignature(0, "Test", "Object", new String[0])
                .withFields(new Field[0])
                .withConstructors(new Constructor[0])
                .withMethods(new Method[0])
                .withAttributes(new Attribute[]{attribute})
                .create();

        when(attribute.getName()).thenReturn(BootstrapMethodsAttribute.ATTRIBUTE_NAME);
        expect(classFile.getBootstrapMethodsAttribute().isPresent()).toBe(true);
        expect(classFile.getBootstrapMethodsAttribute().get()).toBe(attribute);

        when(attribute.getName()).thenReturn("Unknown");
        expect(classFile.getBootstrapMethodsAttribute().isPresent()).toBe(false);
    }



}
