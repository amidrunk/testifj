package org.testifj.lang.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ElementContextMetaDataTest {

    private final ElementContextMetaData exampleMetaData = new ElementContextMetaData(1234, 2345);

    @Test
    public void programCounterCannotBeNegative() {
        expect(() -> new ElementContextMetaData(-1, 100)).toThrow(AssertionError.class);
    }

    @Test
    public void instanceShouldHaveLineNumberAndProgramCounterIfValid() {
        given(new ElementContextMetaData(1234, 2345)).then(it -> {
            expect(it.hasProgramCounter()).toBe(true);
            expect(it.getProgramCounter()).toBe(1234);
            expect(it.hasLineNumber()).toBe(true);
            expect(it.getLineNumber()).toBe(2345);
        });
    }

    @Test
    public void lineNumberShouldNotBeDefinedIfNegative() {
        given(new ElementContextMetaData(1234, -1)).then(it -> {
            expect(it.hasProgramCounter()).toBe(true);
            expect(it.getProgramCounter()).toBe(1234);
            expect(it.hasLineNumber()).toBe(false);
            expect(it::getLineNumber).toThrow(IllegalStateException.class);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleMetaData).toBe(equalTo(exampleMetaData));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleMetaData).not().toBe(equalTo(null));
        expect((Object) exampleMetaData).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final ElementContextMetaData other = new ElementContextMetaData(1234, 2345);

        expect(exampleMetaData).toBe(equalTo(other));
        expect(exampleMetaData.hashCode()).toBe(equalTo(other.hashCode()));
    }


}