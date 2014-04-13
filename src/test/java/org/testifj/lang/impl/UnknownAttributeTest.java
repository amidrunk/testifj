package org.testifj.lang.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class UnknownAttributeTest {

    private final byte[] data = "foo".getBytes();
    private final UnknownAttribute attribute = new UnknownAttribute("anattr", data);

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullName() {
        new UnknownAttribute(null, new byte[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullData() {
        new UnknownAttribute("foo", null);
    }

    @Test
    public void constructorShouldRetainNameAndData() throws IOException {
        assertEquals("anattr", attribute.getName());
        assertArrayEquals(data, IOUtils.toByteArray(attribute.getData()));
    }

    @Test
    public void attributeShouldBeEqualToItSelf() {
        assertEquals(attribute, attribute);
        assertEquals(attribute.hashCode(), attribute.hashCode());
    }

    @Test
    public void attributeShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(attribute, null);
        assertNotEquals(attribute, "foo");
    }

    @Test
    public void attributesWithEqualPropertiesShouldBeEqual() {
        final UnknownAttribute other = new UnknownAttribute("anattr", data);

        assertEquals(attribute, other);
        assertEquals(attribute.hashCode(), other.hashCode());
    }

    @Test
    public void toStringValueShouldContainProperties() {
        assertThat(attribute.toString(), containsString("anattr"));
    }

}
