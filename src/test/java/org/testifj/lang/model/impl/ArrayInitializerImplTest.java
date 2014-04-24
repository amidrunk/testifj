package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.Expression;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class ArrayInitializerImplTest {

    private final Expression exampleValue = Mockito.mock(Expression.class);
    private final ArrayInitializerImpl exampleInitializer = new ArrayInitializerImpl(1234, exampleValue);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new ArrayInitializerImpl(-1, exampleValue)).toThrow(AssertionError.class);
        expect(() -> new ArrayInitializerImpl(0, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        given(exampleInitializer).then(it -> {
            expect(it.getIndex()).toBe(1234);
            expect(it.getValue()).toBe(exampleValue);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleInitializer).toBe(equalTo(exampleInitializer));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleInitializer).not().toBe(equalTo(null));
        expect((Object) exampleInitializer).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final ArrayInitializerImpl other = new ArrayInitializerImpl(1234, exampleValue);

        expect(exampleInitializer).toBe(equalTo(other));
        expect(exampleInitializer.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleInitializer.toString()).then(it -> {
            expect(it).to(containString("1234"));
            expect(it).to(containString(exampleValue.toString()));
        });
    }

}
