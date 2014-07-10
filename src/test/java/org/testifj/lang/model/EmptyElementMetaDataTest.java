package org.testifj.lang.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class EmptyElementMetaDataTest {

    @Test
    public void instanceShouldNotContainProperties() {
        given(new EmptyElementMetaData()).then(it -> {
            expect(it.hasLineNumber()).toBe(false);
            expect(() -> it.getLineNumber()).toThrow(IllegalStateException.class);
            expect(it.hasProgramCounter()).toBe(false);
            expect(() -> it.getProgramCounter()).toThrow(IllegalStateException.class);
        });
    }

}