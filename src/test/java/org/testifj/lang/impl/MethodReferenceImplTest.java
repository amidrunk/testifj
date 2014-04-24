package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.model.Signature;

import java.lang.reflect.Type;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class MethodReferenceImplTest {

    private final Signature signature = mock(Signature.class);
    private final MethodReferenceImpl exampleMethodReference = new MethodReferenceImpl(String.class, "foo", signature);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new MethodReferenceImpl(null, "foo", signature)).toThrow(AssertionError.class);
        expect(() -> new MethodReferenceImpl(mock(Type.class), null, signature)).toThrow(AssertionError.class);
        expect(() -> new MethodReferenceImpl(mock(Type.class), "", signature)).toThrow(AssertionError.class);
        expect(() -> new MethodReferenceImpl(mock(Type.class), "foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        given(exampleMethodReference).then(it -> {
            expect(it.getName()).toBe("foo");
            expect(it.getTargetType()).toBe(String.class);
            expect(it.getSignature()).toBe(signature);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleMethodReference).toBe(equalTo(exampleMethodReference));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect((Object) exampleMethodReference).not().toBe(equalTo("foo"));
        expect(exampleMethodReference).not().toBe(equalTo(null));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final MethodReferenceImpl other = new MethodReferenceImpl(String.class, "foo", signature);

        expect(exampleMethodReference).toBe(equalTo(other));
        expect(exampleMethodReference.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleMethodReference.toString()).then(it -> {
            expect(it).to(containString(String.class.getName()));
            expect(it).to(containString(signature.toString()));
            expect(it).to(containString("foo"));
        });
    }


}
