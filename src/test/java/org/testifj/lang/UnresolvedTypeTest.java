package org.testifj.lang;

import org.junit.Test;
import org.testifj.Given;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class UnresolvedTypeTest {

    @Test
    public void constructorShouldNotAcceptNullOrEmptyTypeName() {
        expect(() -> new UnresolvedType(null)).toThrow(AssertionError.class);
        expect(() -> new UnresolvedType("")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainTypeName() {
        given(new UnresolvedType("com.company.Foo")).then(type -> {
            expect(type.getTypeName()).toBe("com.company.Foo");
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        given(new UnresolvedType("foo")).then(type -> expect(type.equals(type)).toBe(true));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrInstanceOfOtherType() {
        given(new UnresolvedType("foo")).then(type -> {
            expect((Object) type).not().toBe(equalTo("foo"));
            expect(type).not().toBe(equalTo(null));
        });
    }

    @Test
    public void instancesWithEqualTypeNamesShouldBeEqual() {
        given(new UnresolvedType("java.lang.String")).then(type -> {
            expect(type).toBe(equalTo(new UnresolvedType("java.lang.String")));
        });
    }

    @Test
    public void toStringValueShouldContainTypeName() {
        given(new UnresolvedType("java.lang.String")).then(type -> {
            expect(type.toString()).to(containString("java.lang.String"));
        });
    }

}
