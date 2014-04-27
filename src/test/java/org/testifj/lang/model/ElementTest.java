package org.testifj.lang.model;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class ElementTest {

    @Test
    public void asShouldNotAcceptNullType() {
        expect(() -> AST.constant(1).as(null)).toThrow(AssertionError.class);
    }

    @Test
    public void asShouldFailIfTheProvidedTypeIsNotCorrect() {
        expect(() -> AST.constant(1).as(MethodCall.class)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void asShouldReturnSameInstanceButWithNarrowedType() {
        final Element element = AST.constant(1);
        final Constant constant = element.as(Constant.class);

        expect(element).toBe(constant);
    }

}
