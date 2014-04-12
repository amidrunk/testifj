package org.testifj.lang.impl;

import org.testifj.lang.Attribute;
import org.junit.Test;
import org.testifj.lang.impl.DefaultField;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DefaultFieldTest {

    private final Attribute attribute = mock(Attribute.class);
    private final DefaultField field = new DefaultField(1234, "foo", "bar", new Attribute[]{attribute});

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullName() {
        new DefaultField(0, null, "foo", new Attribute[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullSignature() {
        new DefaultField(0, "foo", null, new Attribute[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullAttributes() {
        new DefaultField(0, "foo", "bar", null);
    }

    @Test
    public void constructorShouldRetainParameters() {
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
        final DefaultField other = new DefaultField(1234, "foo", "bar", new Attribute[]{attribute});

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
