package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.*;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class NewArrayImplTest {

    private final ArrayInitializer exampleInitializer = mock(ArrayInitializer.class);

    private final NewArrayImpl exampleArray = new NewArrayImpl(String[].class, String.class, AST.constant(1), Arrays.asList(exampleInitializer));

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new NewArrayImpl(null, String.class, AST.constant(1), Collections.<ArrayInitializer>emptyList())).toThrow(AssertionError.class);
        expect(() -> new NewArrayImpl(String[].class, null, AST.constant(1), Collections.<ArrayInitializer>emptyList())).toThrow(AssertionError.class);
        expect(() -> new NewArrayImpl(String[].class, String.class, null, Collections.<ArrayInitializer>emptyList())).toThrow(AssertionError.class);
        expect(() -> new NewArrayImpl(String[].class, String.class, AST.constant(1), null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        given(exampleArray).then(it -> {
            expect(it.getComponentType()).toBe(String.class);
            expect(it.getLength()).toBe(AST.constant(1));
            expect(it.getType()).toBe(String[].class);
            expect(it.getElementType()).toBe(ElementType.NEW_ARRAY);
            expect(it.getInitializers().toArray()).toBe(new Object[]{exampleInitializer});
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleArray).toBe(equalTo(exampleArray));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleArray).not().toBe(equalTo(null));
        expect((Object) exampleArray).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final NewArrayImpl other = new NewArrayImpl(String[].class, String.class, AST.constant(1), Arrays.asList(exampleInitializer));

        expect(exampleArray).toBe(equalTo(other));
        expect(exampleArray.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleArray.toString()).then(it -> {
            expect(it).to(containString(String.class.getTypeName()));
            expect(it).to(containString(AST.constant(1).toString()));
            expect(it).to(containString(exampleInitializer.toString()));
        });
    }

    @Test
    public void newArrayWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(exampleArray.getMetaData()).not().toBe(equalTo(null));
        expect(new NewArrayImpl(String[].class, String.class, mock(Expression.class), Collections.emptyList(), metaData).getMetaData()).toBe(metaData);
    }

}
