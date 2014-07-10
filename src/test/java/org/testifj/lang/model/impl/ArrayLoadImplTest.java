package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.Expression;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class ArrayLoadImplTest {

    private final ArrayLoadImpl exampleArrayLoad = new ArrayLoadImpl(AST.local("foo", String[].class, 1), AST.constant(1), String.class);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new ArrayLoadImpl(null, AST.constant(1), String.class)).toThrow(AssertionError.class);
        expect(() -> new ArrayLoadImpl(AST.local("foo", String[].class, 1), null, String.class)).toThrow(AssertionError.class);
        expect(() -> new ArrayLoadImpl(AST.local("foo", String[].class, 1), AST.constant(1), null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        given(exampleArrayLoad).then(it -> {
            expect(it.getArray()).toBe(AST.local("foo", String[].class, 1));
            expect(it.getIndex()).toBe(AST.constant(1));
            expect(it.getType()).toBe(String.class);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleArrayLoad).toBe(equalTo(exampleArrayLoad));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleArrayLoad).not().toBe(equalTo(null));
        expect((Object) exampleArrayLoad).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final ArrayLoadImpl other = new ArrayLoadImpl(exampleArrayLoad.getArray(), exampleArrayLoad.getIndex(), exampleArrayLoad.getType());

        expect(exampleArrayLoad).toBe(equalTo(other));
        expect(exampleArrayLoad.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleArrayLoad.toString()).then(str -> {
            expect(str).to(containString(exampleArrayLoad.getArray().toString()));
            expect(str).to(containString(exampleArrayLoad.getIndex().toString()));
            expect(str).to(containString(exampleArrayLoad.getType().toString()));
        });
    }

    @Test
    public void elementWithMetaDataAndDefaultMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        final ArrayLoadImpl arrayLoad1 = new ArrayLoadImpl(mock(Expression.class), mock(Expression.class), String.class);
        final ArrayLoadImpl arrayLoad2 = new ArrayLoadImpl(mock(Expression.class), mock(Expression.class), String.class, metaData);

        expect(arrayLoad1.getMetaData()).not().toBe(equalTo(null));
        expect(arrayLoad2.getMetaData()).toBe(metaData);
    }

}
