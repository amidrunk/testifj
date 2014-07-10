package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class TypeCastImplTest {

    private final TypeCastImpl exampleCast = new TypeCastImpl(AST.constant("foo"), String.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new TypeCastImpl(null, String.class)).toThrow(AssertionError.class);
        expect(() -> new TypeCastImpl(AST.constant("foo"), null)).toThrow(AssertionError.class);
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
        final TypeCastImpl other = new TypeCastImpl(AST.constant("foo"), String.class);

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

    @Test
    public void typeCastWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(exampleCast.getMetaData()).not().toBe(equalTo(null));
        expect(new TypeCastImpl(mock(Expression.class), String.class, metaData).getMetaData()).toBe(metaData);
    }
}
