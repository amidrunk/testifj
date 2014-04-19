package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Signature;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class NewInstanceImplTest {

    private final Signature exampleSignature = mock(Signature.class);
    private final Expression exampleParameter = mock(Expression.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new NewInstanceImpl(null, exampleSignature, Collections.<Expression>emptyList())).toThrow(AssertionError.class);
        expect(() -> new NewInstanceImpl(mock(Type.class), null, Collections.<Expression>emptyList())).toThrow(AssertionError.class);
        expect(() -> new NewInstanceImpl(mock(Type.class), exampleSignature, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        given(new NewInstanceImpl(String.class, exampleSignature, Arrays.asList(exampleParameter))).then(newInstance -> {
            expect(newInstance.getType()).toBe(String.class);
            expect(newInstance.getConstructorSignature()).toBe(exampleSignature);
            expect(newInstance.getParameters().toArray()).toBe(new Object[]{exampleParameter});
        });
    }

    @Test
    public void newInstanceElementShouldBeEqualToItSelf() {
        given(new NewInstanceImpl(String.class, exampleSignature, Collections.<Expression>emptyList())).then(e -> {
            expect(e.equals(e)).toBe(true);
            expect(e.hashCode()).toBe(e.hashCode());
        });
    }

    @Test
    public void newInstanceShouldNotBeEqualToNullOrIncorrectType() {
        given(new NewInstanceImpl(String.class, exampleSignature, Collections.<Expression>emptyList())).then(it -> {
            expect(it.equals(null)).toBe(false);
            expect(it.equals("foo")).toBe(false);
        });
    }

    @Test
    public void newInstanceElementsWithEqualPropertiesShouldBeEqual() {
        final NewInstanceImpl newInstance1 = new NewInstanceImpl(String.class, exampleSignature, Collections.<Expression>emptyList());
        final NewInstanceImpl newInstance2 = new NewInstanceImpl(String.class, exampleSignature, Collections.<Expression>emptyList());

        expect(newInstance1).toBe(equalTo(newInstance2));
        expect(newInstance1.hashCode()).toBe(equalTo(newInstance2.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(new NewInstanceImpl(String.class, exampleSignature, Arrays.asList(exampleParameter))).then(it -> {
            expect(it.toString()).to(containString(String.class.getName()));
            expect(it.toString()).to(containString(exampleSignature.toString()));
            expect(it.toString()).to(containString(exampleParameter.toString()));
        });
    }

}
