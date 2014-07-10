package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Goto;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class GotoImplTest {

    private final Goto exampleElement = new GotoImpl(1234);

    @Test
    public void constructorShouldNotAcceptNegativeProgramCounter() {
        expect(() -> new GotoImpl(-1)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        expect(exampleElement.getTargetProgramCounter()).toBe(1234);
        expect(exampleElement.getElementType()).toBe(ElementType.GOTO);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleElement).toBe(equalTo(exampleElement));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleElement).not().toBe(equalTo(null));
        expect((Object) exampleElement).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Goto other = new GotoImpl(1234);

        expect(exampleElement).toBe(equalTo(other));
        expect(exampleElement.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void gotoWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = Mockito.mock(ElementMetaData.class);

        expect(exampleElement.getMetaData()).not().toBe(equalTo(null));
        expect(new GotoImpl(1234, metaData).getMetaData()).toBe(metaData);
    }

}