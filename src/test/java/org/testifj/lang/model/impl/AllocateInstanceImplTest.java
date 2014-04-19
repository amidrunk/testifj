package org.testifj.lang.model.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class AllocateInstanceImplTest {

    @Test
    public void constructorShouldNotAcceptNullArg() {
        expect(() -> new AllocateInstanceImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainType() {
        given(new AllocateInstanceImpl(String.class)).then(e -> {
            expect(e.getType()).toBe(String.class);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        given(new AllocateInstanceImpl(String.class)).then(e -> {
            expect(e.equals(e)).toBe(true);
            expect(e.hashCode()).toBe(e.hashCode());
        });
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        given(new AllocateInstanceImpl(String.class)).then(e -> {
            expect(e.equals(null)).toBe(false);
            expect(e.equals("foo")).toBe(false);
        });
    }

    @Test
    public void instancesWithEqualTypesShouldBeEqual() {
        final AllocateInstanceImpl e1 = new AllocateInstanceImpl(String.class);
        final AllocateInstanceImpl e2 = new AllocateInstanceImpl(String.class);

        expect(e1).toBe(equalTo(e2));
        expect(e1.hashCode()).toBe(equalTo(e2.hashCode()));
    }

    @Test
    public void instancesWithDifferentTypesShouldNotBeEqual() {
        final AllocateInstanceImpl e1 = new AllocateInstanceImpl(String.class);
        final AllocateInstanceImpl e2 = new AllocateInstanceImpl(Integer.class);

        expect(e1).not().toBe(equalTo(e2));
        expect(e1.hashCode()).not().toBe(equalTo(e2.hashCode()));
    }

    @Test
    public void toStringValueShouldContainTypeName() {
        given(new AllocateInstanceImpl(String.class)).then(e -> {
            expect(e.toString()).to(containString(String.class.getName()));
        });
    }

}
