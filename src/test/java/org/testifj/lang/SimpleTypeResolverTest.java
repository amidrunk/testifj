package org.testifj.lang;

import org.junit.Test;
import org.testifj.lang.impl.SimpleTypeResolver;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.instanceOf;

public class SimpleTypeResolverTest {

    private final SimpleTypeResolver resolver = new SimpleTypeResolver();

    @Test
    public void resolveTypeShouldNotAcceptNullOrEmptyTypeName() {
        expect(() -> resolver.resolveType(null)).toThrow(AssertionError.class);
        expect(() -> resolver.resolveType("")).toThrow(AssertionError.class);
    }

    @Test
    public void resolveTypeShouldReturnClassIfClassIsFoundInContext() {
        given(resolver.resolveType("java.lang.String")).then(type -> {
            expect(type).toBe(String.class);
        });
    }

    @Test
    public void resolveTypeShouldReturnUnresolvedTypeForUnknownType() {
        given(resolver.resolveType("foo")).then(type -> {
            expect(type).toBe(instanceOf(UnresolvedType.class));
            expect(type.getTypeName()).toBe("foo");
        });
    }

}
