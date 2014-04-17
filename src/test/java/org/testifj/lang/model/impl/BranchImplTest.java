package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class BranchImplTest {

    private final Expression rightOperand = mock(Expression.class);

    private final Expression leftOperand = mock(Expression.class);

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new BranchImpl(null, OperatorType.NE, rightOperand, 1234)).toThrow(AssertionError.class);
        expect(() -> new BranchImpl(leftOperand, null, rightOperand, 1234)).toThrow(AssertionError.class);
        expect(() -> new BranchImpl(leftOperand, OperatorType.NE, null, 1234)).toThrow(AssertionError.class);
        expect(() -> new BranchImpl(leftOperand, OperatorType.NE, rightOperand, -1)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final BranchImpl branch = new BranchImpl(leftOperand, OperatorType.NE, rightOperand, 1234);

        expect(branch.getLeftOperand()).toBe(leftOperand);
        expect(branch.getOperatorType()).toBe(OperatorType.NE);
        expect(branch.getRightOperand()).toBe(rightOperand);
        expect(branch.getTargetPC()).toBe(1234);
        expect(branch.getElementType()).toBe(ElementType.BRANCH);
    }

}
