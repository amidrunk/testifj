package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Cast;
import org.testifj.lang.model.ElementType;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class CastImplTest {

    private final CastImpl exampleCast = new CastImpl(AST.constant("foo"), String.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new CastImpl(null, String.class)).toThrow(AssertionError.class);
        expect(() -> new CastImpl(AST.constant("foo"), null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        given(exampleCast).then(it -> {
            expect(it.getValue()).toBe(AST.constant("foo"));
            expect(it.getType()).toBe(String.class);
            expect(it.getElementType()).toBe(ElementType.CAST);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleCast).toBe(equalTo(exampleCast));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleCast).not().toBe(equalTo(null));
        expect((Object) exampleCast).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final CastImpl other = new CastImpl(AST.constant("foo"), String.class);

        expect(exampleCast).toBe(equalTo(other));
        expect(exampleCast.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleCast.toString()).then(it -> {
            expect(it).to(containString(AST.constant("foo").toString()));
            expect(it).to(containString(String.class.getName()));
        });
    }
}
