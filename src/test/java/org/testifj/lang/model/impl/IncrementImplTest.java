package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Increment;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class IncrementImplTest {

    private final Increment exampleIncrement = new IncrementImpl(AST.constant(1));

    @Test
    public void constructorShouldNotAcceptNullOperand() {
        expect(() -> new IncrementImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        given(exampleIncrement).then(it -> {
            expect(it.getElementType()).toBe(ElementType.INCREMENT);
            expect(it.getType()).toBe(int.class);
            expect(it.getOperand()).toBe(AST.constant(1));
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleIncrement).toBe(equalTo(exampleIncrement));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleIncrement).not().toBe(equalTo(null));
        expect((Object) exampleIncrement).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualOperandsShouldBeEqual() {
        final Increment other = new IncrementImpl(AST.constant(1));

        expect(exampleIncrement).toBe(equalTo(other));
        expect(exampleIncrement.hashCode()).toBe(equalTo(other.hashCode()));
    }

}