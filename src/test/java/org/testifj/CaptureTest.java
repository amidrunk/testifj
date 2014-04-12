package org.testifj;

import org.junit.Test;

import static org.junit.Assert.*;

public class CaptureTest {

    private final Capture<String> capture = new Capture<>();

    @Test(expected = IllegalStateException.class)
    public void getShouldThrowExceptionIfValueHasNotBeenCaptured() {
        capture.get();
    }

    @Test
    public void getShouldReturnCapturedValue() {
        capture.set("foo");

        assertEquals("foo", capture.get());
    }

    @Test
    public void setShouldFailIfAlreadyCaptured() {
        capture.set("foo");

        try {
            capture.set("foo");
            fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void isCapturedShouldReturnFalseIfObjectIsNotCaptured() {
        assertFalse(capture.isCaptured());
    }

    @Test
    public void isCapturedShouldReturnTrueIfObjectIsCaptured() {
        capture.set("foo");
        assertTrue(capture.isCaptured());
    }

}
