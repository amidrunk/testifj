package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.Expression;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class ArrayStoreImplTest {

    private final Expression array = mock(Expression.class, "array");
    private final Expression index = mock(Expression.class, "index");
    private final Expression value = mock(Expression.class, "value");
    private final ArrayStoreImpl exampleArrayStore = new ArrayStoreImpl(array, index, value);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new ArrayStoreImpl(null, index, value)).toThrow(AssertionError.class);
        expect(() -> new ArrayStoreImpl(array, null, value)).toThrow(AssertionError.class);
        expect(() -> new ArrayStoreImpl(array, null, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        given(exampleArrayStore).then(it -> {
            expect(it.getArray()).toBe(array);
            expect(it.getIndex()).toBe(index);
            expect(it.getValue()).toBe(value);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleArrayStore).toBe(equalTo(exampleArrayStore));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleArrayStore).not().toBe(equalTo(null));
        expect((Object) exampleArrayStore).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final ArrayStoreImpl other = new ArrayStoreImpl(array, index, value);

        expect(exampleArrayStore).toBe(equalTo(other));
        expect(exampleArrayStore.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleArrayStore.toString()).then(it -> {
            expect(it).to(containString(array.toString()));
            expect(it).to(containString(index.toString()));
            expect(it).to(containString(value.toString()));
        });
    }

    @Test
    public void arrayStoreWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(new ArrayStoreImpl(array, index, value).getMetaData()).not().toBe(equalTo(null));
        expect(new ArrayStoreImpl(array, index, value, metaData).getMetaData()).toBe(metaData);
    }

}
