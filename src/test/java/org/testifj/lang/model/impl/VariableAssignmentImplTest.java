package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;

import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;

public class VariableAssignmentImplTest {

    private final Expression exampleValue = Mockito.mock(Expression.class);

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new VariableAssignmentImpl(null, "foo", String.class)).toThrow(AssertionError.class);
        expect(() -> new VariableAssignmentImpl(exampleValue, null, String.class)).toThrow(AssertionError.class);
        expect(() -> new VariableAssignmentImpl(exampleValue, "", String.class)).toThrow(AssertionError.class);
        expect(() -> new VariableAssignmentImpl(exampleValue, "foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        final VariableAssignmentImpl assignment = new VariableAssignmentImpl(exampleValue, "foo", String.class);

        when(exampleValue.getType()).thenReturn(Integer.class);

        expect(assignment.getElementType()).toBe(ElementType.VARIABLE_ASSIGNMENT);
        expect(assignment.getType()).toBe(Integer.class);
        expect(assignment.getValue()).toBe(exampleValue);
        expect(assignment.getVariableName()).toBe("foo");
        expect(assignment.getVariableType()).toBe(String.class);
    }

}
