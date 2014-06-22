package org.testifj.lang.model;

import org.junit.Test;
import org.testifj.lang.model.impl.IncrementImpl;
import org.testifj.lang.model.impl.VariableAssignmentImpl;
import org.testifj.matchers.core.OptionalThatIs;

import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.*;
import static org.testifj.lang.model.ModelQueries.affixIsUndefined;
import static org.testifj.lang.model.ModelQueries.leftOperand;
import static org.testifj.lang.model.ModelQueries.rightOperand;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class ModelQueriesTest {

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
    public void ofTypeShouldNotAcceptNullType() {
        expect(() -> ModelQueries.ofType(null)).toThrow(AssertionError.class);
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
}