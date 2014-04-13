package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.Attribute;
import org.testifj.lang.ClassFile;

import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;

@SuppressWarnings("unchecked")
public class DefaultFieldTest {

    private final Attribute attribute = mock(Attribute.class);

    private final Supplier<ClassFile> classFileSupplier = mock(Supplier.class);

    private final DefaultField field = new DefaultField(classFileSupplier, 1234, "foo", "bar", new Attribute[]{attribute});

    @Test
    public void constructorShouldNotAcceptNullClassFileSupplier() {
        expect(() -> new DefaultField(null, 0, "foo", "bar", new Attribute[0])).toThrow(AssertionError.class);
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullName() {
        new DefaultField(classFileSupplier, 0, null, "foo", new Attribute[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullSignature() {
        new DefaultField(classFileSupplier, 0, "foo", null, new Attribute[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullAttributes() {
        new DefaultField(classFileSupplier, 0, "foo", "bar", null);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final ClassFile classFile = mock(ClassFile.class);

        when(classFileSupplier.get()).thenReturn(classFile);

        assertEquals(classFile, field.getClassFile());
        assertEquals(1234, field.getAccessFlags());
        assertEquals("foo", field.getName());
        assertEquals("bar", field.getSignature());
        assertArrayEquals(new Attribute[]{attribute}, field.getAttributes().toArray());
    }

    @Test
    public void fieldShouldBeEqualToItSelf() {
        assertEquals(field, field);
        assertEquals(field.hashCode(), field.hashCode());
    }

    @Test
    public void fieldShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(field, null);
        assertNotEquals(field, "foo");
    }

    @Test
    public void fieldsWithEqualPropertiesShouldBeEqual() {
        final DefaultField other = new DefaultField(classFileSupplier, 1234, "foo", "bar", new Attribute[]{attribute});

        assertEquals(field, other);
        assertEquals(field.hashCode(), other.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertThat(field.toString(), containsString("1234"));
        assertThat(field.toString(), containsString("foo"));
        assertThat(field.toString(), containsString("bar"));
        assertThat(field.toString(), containsString(attribute.toString()));
    }

}
