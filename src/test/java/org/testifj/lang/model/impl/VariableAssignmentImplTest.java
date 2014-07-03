package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.IncompatibleTypeException;
import org.testifj.lang.model.VariableAssignment;

import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class VariableAssignmentImplTest {

    private final Expression exampleValue = Mockito.mock(Expression.class);
    private final VariableAssignmentImpl assignment = new VariableAssignmentImpl(exampleValue, 123, "foo", String.class);

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new VariableAssignmentImpl(null, 0, "foo", String.class)).toThrow(AssertionError.class);
        expect(() -> new VariableAssignmentImpl(exampleValue, -1, "foo", String.class)).toThrow(AssertionError.class);
        expect(() -> new VariableAssignmentImpl(exampleValue, 0, null, String.class)).toThrow(AssertionError.class);
        expect(() -> new VariableAssignmentImpl(exampleValue, 0, "", String.class)).toThrow(AssertionError.class);
        expect(() -> new VariableAssignmentImpl(exampleValue, 0, "foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        when(exampleValue.getType()).thenReturn(Integer.class);

        expect(assignment.getElementType()).toBe(ElementType.VARIABLE_ASSIGNMENT);
        expect(assignment.getValue()).toBe(exampleValue);
        expect(assignment.getVariableIndex()).toBe(123);
        expect(assignment.getVariableName()).toBe("foo");
        expect(assignment.getVariableType()).toBe(String.class);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(assignment).toBe(equalTo(assignment));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(assignment).not().toBe(equalTo(null));
        expect((Object) assignment).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final VariableAssignmentImpl other = new VariableAssignmentImpl(exampleValue, 123, "foo", String.class);

        expect(assignment).toBe(equalTo(other));
        expect(assignment.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(assignment.toString()).then(it -> {
            expect(it).to(containString(exampleValue.toString()));
            expect(it).to(containString("123"));
            expect(it).to(containString("foo"));
            expect(it).to(containString(String.class.toString()));
        });
    }

    @Test
    public void withValueShouldNotAcceptInvalidArg() {
        expect(() -> assignment.withValue(null)).toThrow(AssertionError.class);
    }

    @Test
    public void withValueShouldFailIfValueIsNotAssignableToType() {
        expect(() -> assignment.withValue(constant(1))).toThrow(IncompatibleTypeException.class);
    }

    @Test
    public void withValueShouldReturnNewAssignmentWithNewValue() {
        final VariableAssignment newAssignment = assignment.withValue(constant("foobar"));

        expect(newAssignment.getVariableName()).toBe(assignment.getVariableName());
        expect(newAssignment.getVariableIndex()).toBe(assignment.getVariableIndex());
        expect(newAssignment.getVariableType()).toBe(assignment.getVariableType());
        expect(newAssignment.getValue()).toBe(constant("foobar"));
        expect(assignment.getValue()).toBe(exampleValue);
    }

}
