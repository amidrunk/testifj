package org.testifj.lang.impl;

import org.testifj.lang.Attribute;
import org.junit.Test;
import org.testifj.lang.impl.DefaultMethod;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DefaultMethodTest {

    private final Attribute attribute = mock(Attribute.class);
    private final DefaultMethod method = new DefaultMethod(1234, "foo", "bar", new Attribute[]{attribute});

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullName() {
        new DefaultMethod(0, null, "foo", new Attribute[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullSignature() {
        new DefaultMethod(0, "foo", null, new Attribute[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullAttributes() {
        new DefaultMethod(0, "foo", "bar", null);
    }

    @Test
    public void constructorShouldRetainParameters() {
        assertEquals(1234, method.getAccessFlags());
        assertEquals("foo", method.getName());
        assertEquals("bar", method.getSignature());
        assertArrayEquals(new Attribute[]{attribute}, method.getAttributes().toArray());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(method, method);
        assertEquals(method.hashCode(), method.hashCode());
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(method, null);
        assertNotEquals(method, "foo");
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final DefaultMethod other = new DefaultMethod(1234, "foo", "bar", new Attribute[]{attribute});

        assertEquals(1234, other.getAccessFlags());
        assertEquals("foo", other.getName());
        assertEquals("bar", other.getSignature());
        assertArrayEquals(new Attribute[]{attribute}, other.getAttributes().toArray());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertThat(method.toString(), containsString("1234"));
        assertThat(method.toString(), containsString("foo"));
        assertThat(method.toString(), containsString("bar"));
        assertThat(method.toString(), containsString(attribute.toString()));
    }

}
