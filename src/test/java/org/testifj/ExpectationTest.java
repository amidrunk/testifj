package org.testifj;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class ExpectationTest {

    @Test
    public void expectationArgumentCanBeCaptured() {
        final Capture<String> verifiedString = new Capture<>();
        final OutcomeExpectation<String> expectation = verifiedString::set;

        final Capture<String> capture = new Capture<>();

        expectation.capture(capture).verify("foo");

        assertEquals("foo", capture.get());
        assertEquals("foo", verifiedString.get());
    }

}
