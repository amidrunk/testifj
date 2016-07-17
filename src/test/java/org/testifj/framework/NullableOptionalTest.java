package org.testifj.framework;

import org.junit.Test;

import static org.junit.Assert.*;

public class NullableOptionalTest {

    @Test
    public void optionalOfValidInstanceCanBeCreated() {
        final NullableOptional<String> optional = NullableOptional.of("foo");

        assertTrue(optional.isPresent());
        assertEquals("foo", optional.get());
    }

    @Test
    public void optionalOfNullCanBeCreated() {
        final NullableOptional<String> optional = NullableOptional.of(null);

        assertTrue(optional.isPresent());
        assertNull(optional.get());
    }

    @Test
    public void absentOptionalCanBeCreated() {
        final NullableOptional<String> optional = NullableOptional.empty();

        assertFalse(optional.isPresent());

        boolean failed = false;

        try {
            optional.get();
        } catch (IllegalStateException e) {
            failed = true;
        }

        assertTrue(failed);
    }
}