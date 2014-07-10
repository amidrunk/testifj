package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

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
        expect(exampleOperator.getLeftOperand()).toBe(leftOperand);
        expect(exampleOperator.getOperatorType()).toBe(OperatorType.PLUS);
        expect(exampleOperator.getRightOperand()).toBe(rightOperand);
        expect(exampleOperator.getType()).toBe(int.class);
    }

    @Test
    public void elementTypeShouldBeSpecified() {
        expect(exampleOperator.getElementType()).toBe(ElementType.BINARY_OPERATOR);
    }

    @Test
    public void elementWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(new BinaryOperatorImpl(leftOperand, OperatorType.AND, rightOperand, int.class).getMetaData()).not().toBe(equalTo(null));
        expect(new BinaryOperatorImpl(leftOperand, OperatorType.AND, rightOperand, int.class, metaData).getMetaData()).toBe(metaData);
    }

}
