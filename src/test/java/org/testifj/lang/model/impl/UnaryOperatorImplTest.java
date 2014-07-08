package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.OperatorType;
import org.testifj.lang.model.UnaryOperator;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class UnaryOperatorImplTest {

    private final UnaryOperator exampleOperator = new UnaryOperatorImpl(constant(true), OperatorType.NOT, boolean.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new UnaryOperatorImpl(null, OperatorType.NOT, String.class)).toThrow(AssertionError.class);
        expect(() -> new UnaryOperatorImpl(constant(true), null, String.class)).toThrow(AssertionError.class);
        expect(() -> new UnaryOperatorImpl(constant(true), OperatorType.NOT, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        expect(exampleOperator.getOperand()).toBe(constant(true));
        expect(exampleOperator.getOperatorType()).toBe(OperatorType.NOT);
        expect(exampleOperator.getElementType()).toBe(ElementType.UNARY_OPERATOR);
        expect(exampleOperator.getType()).toBe(boolean.class);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleOperator).toBe(equalTo(exampleOperator));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleOperator).not().toBe(equalTo(null));
        expect((Object) exampleOperator).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final UnaryOperator other = new UnaryOperatorImpl(constant(true), OperatorType.NOT, boolean.class);

        expect(exampleOperator).toBe(equalTo(other));
        expect(exampleOperator.hashCode()).toBe(equalTo(other.hashCode()));
    }


}