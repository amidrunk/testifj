package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementMetaData;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class InstanceAllocationImplTest {

    @Test
    public void constructorShouldNotAcceptNullArg() {
        expect(() -> new InstanceAllocationImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainType() {
        given(new InstanceAllocationImpl(String.class)).then(e -> {
            expect(e.getType()).toBe(String.class);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        given(new InstanceAllocationImpl(String.class)).then(e -> {
            expect(e.equals(e)).toBe(true);
            expect(e.hashCode()).toBe(e.hashCode());
        });
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        given(new InstanceAllocationImpl(String.class)).then(e -> {
            expect(e.equals(null)).toBe(false);
            expect(e.equals("foo")).toBe(false);
        });
    }

    @Test
    public void instancesWithEqualTypesShouldBeEqual() {
        final InstanceAllocationImpl e1 = new InstanceAllocationImpl(String.class);
        final InstanceAllocationImpl e2 = new InstanceAllocationImpl(String.class);

        expect(e1).toBe(equalTo(e2));
        expect(e1.hashCode()).toBe(equalTo(e2.hashCode()));
    }

    @Test
    public void instancesWithDifferentTypesShouldNotBeEqual() {
        final InstanceAllocationImpl e1 = new InstanceAllocationImpl(String.class);
        final InstanceAllocationImpl e2 = new InstanceAllocationImpl(Integer.class);

        expect(e1).not().toBe(equalTo(e2));
        expect(e1.hashCode()).not().toBe(equalTo(e2.hashCode()));
    }

    @Test
    public void toStringValueShouldContainTypeName() {
        given(new InstanceAllocationImpl(String.class)).then(e -> {
            expect(e.toString()).to(containString(String.class.getName()));
        });
    }

    @Test
    public void instanceWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(new InstanceAllocationImpl(String.class).getMetaData()).not().toBe(equalTo(null));
        expect(new InstanceAllocationImpl(String.class, metaData).getMetaData()).toBe(equalTo(metaData));
    }

}
