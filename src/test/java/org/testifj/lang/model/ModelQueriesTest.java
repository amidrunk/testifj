package org.testifj.lang.model;

import org.junit.Test;
import org.testifj.lang.model.impl.IncrementImpl;
import org.testifj.lang.model.impl.VariableAssignmentImpl;
import org.testifj.matchers.core.ObjectThatIs;
import org.testifj.matchers.core.OptionalThatIs;

import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.*;
import static org.testifj.lang.model.ModelQueries.*;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class ModelQueriesTest {

    @Test
    public void ofTypeShouldNotAcceptNullElementType() {
        expect(() -> ModelQueries.ofType((ElementType) null)).toThrow(AssertionError.class);
    }

    @Test
    public void ofTypeShouldNotMatchElementOfDifferentType() {
        expect(ModelQueries.ofType(ElementType.METHOD_CALL).test(constant(1))).toBe(false);
    }

    @Test
    public void ofTypeShouldMatchElementOfEqualType() {
        expect(ModelQueries.ofType(ElementType.CONSTANT).test(constant(1))).toBe(true);
    }

    @Test
    public void ofTypeShouldNotMatchNull() {
        expect(ModelQueries.ofType(ElementType.CONSTANT).test(null)).toBe(false);
    }

    @Test
    public void isAssignmentToShouldNotAcceptNullLocalVariableReference() {
        expect(() -> ModelQueries.isAssignmentTo(null)).toThrow(AssertionError.class);
    }

    @Test
    public void isAssignmentToShouldNotMatchIfAssignedVariableDoesNotMatch() {
        final boolean result = ModelQueries.isAssignmentTo(AST.local("foo", String.class, 1))
                .test(AST.set(AST.local("bar", String.class, 2)).to(constant(1)));

        expect(result).toBe(false);
    }

    @Test
    public void isAssignmentShouldMatchIfAssignedVariableMatches() {
        final boolean result = ModelQueries.isAssignmentTo(AST.local("foo", String.class, 1))
                .test(AST.set(AST.local("foo", String.class, 1)).to(constant(1)));

        expect(result).toBe(true);
    }

    @Test
    public void isAssignmentToShouldNotMatchNull() {
        final boolean result = ModelQueries.isAssignmentTo(AST.local("foo", String.class, 1)).test(null);

        expect(result).toBe(false);
    }

    @Test
    public void assignedValueShouldReturnValueOfVariableAssignment() {
        final Constant expectedValue = constant(1);

        final Expression value = ModelQueries.assignedValue()
                .from(AST.set(AST.local("foo", String.class, 1)).to(expectedValue))
                .get();

        expect(value).toBe(expectedValue);
    }

    @Test
    public void assignedValueShouldReturnNonPresentOptionalForNullAssignment() {
        expect(ModelQueries.assignedValue().from(null)).not().toBe(present());
    }

    @Test
    public void isCastToShouldNotAcceptNullType() {
        expect(() -> ModelQueries.isCastTo(null)).toThrow(AssertionError.class);
    }

    @Test
    public void isCastToShouldMatchCastWithCorrectTargetType() {
        expect(ModelQueries.isCastTo(byte.class).test(cast(constant(1)).to(byte.class))).toBe(true);
    }

    @Test
    public void isCastToShouldNotMatchCastWithDifferentTargetType() {
        expect(ModelQueries.isCastTo(byte.class).test(cast(constant(1)).to(int.class))).toBe(false);
    }

    @Test
    public void castValueShouldReturnNonPresentInstanceForNullCast() {
        expect(ModelQueries.castValue().from(null)).not().toBe(present());
    }

    @Test
    public void castValueShouldReturnValueOfCast() {
        final Expression value = constant(1);

        expect(ModelQueries.castValue().from(cast(value).to(byte.class))).toBe(optionalOf(value));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ofTypeShouldNotAcceptNullType() {
        expect(() -> ModelQueries.ofType((Class) null)).toThrow(AssertionError.class);
    }

    @Test
    public void ofTypeShouldMatchInstanceOfMatchingType() {
        expect(ModelQueries.ofType(String.class).test("foo")).toBe(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ofTypeShouldNotMatchInstanceOfDifferentType() {
        expect(((Predicate) ModelQueries.ofType(String.class)).test(1234)).toBe(false);
    }

    @Test
    public void leftOperandShouldNotBePresentForNullInstance() {
        expect(leftOperand().from(null)).not().toBe(present());
    }

    @Test
    public void leftOperandShouldReturnLeftOperand() {
        expect(leftOperand().from(AST.add(constant(1), constant(2), int.class))).toBe(optionalOf(constant(1)));
    }

    @Test
    public void rightOperandShouldNotBePresentForNullInstance() {
        expect(rightOperand().from(null)).not().toBe(present());
    }

    @Test
    public void rightOperandShouldReturnRightOperand() {
        expect(rightOperand().from(add(constant(1), constant(2), int.class))).toBe(optionalOf(constant(2)));
    }

    @Test
    public void affixIsUndefinedShouldReturnFalseForNull() {
        expect(affixIsUndefined().test(null)).toBe(false);
    }

    @Test
    public void affixIsUndefinedShouldNotMatchIncrementWithAffix() {
        expect(affixIsUndefined().test(new IncrementImpl(local("foo", int.class, 1), constant(1), int.class, Affix.PREFIX))).toBe(false);
        expect(affixIsUndefined().test(new IncrementImpl(local("foo", int.class, 1), constant(1), int.class, Affix.POSTFIX))).toBe(false);
    }

    @Test
    public void affixIsUndefinedShouldMatchIncrementWithUndefinedAffix() {
        expect(affixIsUndefined().test(new IncrementImpl(local("foo", int.class, 1), constant(1), int.class, Affix.UNDEFINED))).toBe(true);
    }

    @Test
    public void assignedVariableTypeIsShouldNotAcceptNullType() {
        expect(() -> ModelQueries.assignedVariableTypeIs(null)).toThrow(AssertionError.class);
    }

    @Test
    public void assignedVariableTypeIsShouldNotMatchNull() {
        expect(ModelQueries.assignedVariableTypeIs(String.class).test(null)).toBe(false);
    }

    @Test
    public void assignedVariableTypeShouldNotMatchAssignmentWithDifferentVariableType() {
        final VariableAssignment variableAssignment = set(local("foo", String.class, 1)).to(constant("bar"));

        expect(ModelQueries.assignedVariableTypeIs(int.class).test(variableAssignment)).toBe(false);
    }

    @Test
    public void assignedVariableTypeShouldMatchAssignmentWithMatchingVariableType() {
        final VariableAssignment variableAssignment = set(local("foo", String.class, 1)).to(constant("bar"));

        expect(ModelQueries.assignedVariableTypeIs(String.class).test(variableAssignment)).toBe(true);
    }

    @Test
    public void ofRuntimeTypeShouldNotAcceptNullType() {
        expect(() -> ModelQueries.ofRuntimeType(null)).toThrow(AssertionError.class);
    }

    @Test
    public void ofRuntimeTypeShouldNotMatchNullExpression() {
        expect(ModelQueries.ofRuntimeType(String.class).test(null)).toBe(false);
    }

    @Test
    public void ofRuntimeTypeShouldNotMatchExpressionWithDifferentType() {
        final Expression expression = mock(Expression.class);

        when(expression.getType()).thenReturn(String.class);

        expect(ModelQueries.ofRuntimeType(Object.class).test(expression)).toBe(false);
    }

    @Test
    public void ofRuntimeTypeShouldMatchExpressionWithEqualType() {
        final Expression expression = mock(Expression.class);

        when(expression.getType()).thenReturn(String.class);

        expect(ModelQueries.ofRuntimeType(String.class).test(expression)).toBe(true);
    }
    
    @Test
    public void leftComparativeOperandShouldReturnNonPresentOptionalForNull() {
        expect(ModelQueries.leftComparativeOperand().from(null)).not().toBe(present());
    }

    @Test
    public void leftComparativeOperandShouldReturnLeftOperandOfBranch() {
        final Branch branch = mock(Branch.class);
        final Expression operand = mock(Expression.class);

        when(branch.getLeftOperand()).thenReturn(operand);

        expect(ModelQueries.leftComparativeOperand().from(branch)).toBe(optionalOf(operand));
    }

    @Test
    public void rightComparativeOperandShouldReturnNonPresentOptionalForNull() {
        expect(ModelQueries.rightComparativeOperand().from(null)).not().toBe(present());
    }

    @Test
    public void rightComparativeOperandShouldReturnOptionalOfRightOperand() {
        final Branch branch = mock(Branch.class);
        final Expression operand = mock(Expression.class);

        when(branch.getRightOperand()).thenReturn(operand);

        expect(ModelQueries.rightComparativeOperand().from(branch)).toBe(optionalOf(operand));
    }
}