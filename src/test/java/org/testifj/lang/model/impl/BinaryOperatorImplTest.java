package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class BinaryOperatorImplTest {

    private final Expression rightOperand = mock(Expression.class);
    private final Expression leftOperand = mock(Expression.class);
    private final BinaryOperatorImpl exampleOperator = new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, int.class);

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new BinaryOperatorImpl(null, OperatorType.PLUS, rightOperand, int.class)).toThrow(AssertionError.class);
        expect(() -> new BinaryOperatorImpl(leftOperand, null, rightOperand, int.class)).toThrow(AssertionError.class);
        expect(() -> new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, null, int.class)).toThrow(AssertionError.class);
        expect(() -> new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeOperatorsAndOperatorType() {
        expect(exampleOperator.getLeftOperand()).to(equal(leftOperand));
        expect(exampleOperator.getOperatorType()).to(equal(OperatorType.PLUS));
        expect(exampleOperator.getRightOperand()).to(equal(rightOperand));
        expect(exampleOperator.getType()).to(equal(int.class));
    }

    @Test
    public void elementTypeShouldBeSpecified() {
        expect(exampleOperator.getElementType()).to(equal(ElementType.BINARY_OPERATOR));
    }

}
