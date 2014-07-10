package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.*;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class BranchImplTest {

    private final Expression rightOperand = mock(Expression.class);

    private final Expression leftOperand = mock(Expression.class);
    private final Branch exampleBranch = new BranchImpl(leftOperand, OperatorType.NE, rightOperand, 1234);

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new BranchImpl(null, OperatorType.NE, rightOperand, 1234)).toThrow(AssertionError.class);
        expect(() -> new BranchImpl(leftOperand, null, rightOperand, 1234)).toThrow(AssertionError.class);
        expect(() -> new BranchImpl(leftOperand, OperatorType.NE, null, 1234)).toThrow(AssertionError.class);
        expect(() -> new BranchImpl(leftOperand, OperatorType.NE, rightOperand, -1)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        expect(exampleBranch.getLeftOperand()).toBe(leftOperand);
        expect(exampleBranch.getOperatorType()).toBe(OperatorType.NE);
        expect(exampleBranch.getRightOperand()).toBe(rightOperand);
        expect(exampleBranch.getTargetProgramCounter()).toBe(1234);
        expect(exampleBranch.getElementType()).toBe(ElementType.BRANCH);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleBranch).toBe(equalTo(exampleBranch));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleBranch).not().toBe(equalTo(null));
        expect((Object) exampleBranch).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Branch other = new BranchImpl(leftOperand, OperatorType.NE, rightOperand, 1234);

        expect(exampleBranch).toBe(equalTo(other));
        expect(exampleBranch.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void branchWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(new BranchImpl(leftOperand, OperatorType.EQ, rightOperand, 1234).getMetaData()).not().toBe(equalTo(null));
        expect(new BranchImpl(leftOperand, OperatorType.EQ, rightOperand, 1234, metaData).getMetaData()).toBe(metaData);
    }

}
