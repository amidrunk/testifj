package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.FieldReference;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class FieldAssignmentImplTest {

    private final Expression expression = mock(Expression.class);
    private final FieldReference fieldReference = mock(FieldReference.class);
    private final FieldAssignmentImpl exampleAssignment = new FieldAssignmentImpl(fieldReference, expression);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new FieldAssignmentImpl(null, expression)).toThrow(AssertionError.class);
        expect(() -> new FieldAssignmentImpl(fieldReference, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        given(exampleAssignment).then(assignment -> {
            expect(assignment.getFieldReference()).toBe(fieldReference);
            expect(assignment.getValue()).toBe(expression);
            expect(assignment.getElementType()).toBe(ElementType.FIELD_ASSIGNMENT);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleAssignment).toBe(equalTo(exampleAssignment));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleAssignment).not().toBe(equalTo(null));
        expect((Object) exampleAssignment).not().toBe(equalTo("foo"));
    }

    @Test
    public void instanceWithEqualPropertiesShouldBeEqual() {
        final FieldAssignmentImpl other = new FieldAssignmentImpl(fieldReference, expression);

        expect(exampleAssignment).toBe(equalTo(other));
        expect(exampleAssignment.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleAssignment.toString()).then(it -> {
            expect(it).to(containString(fieldReference.toString()));
            expect(it).to(containString(expression.toString()));
        });
    }

    @Test
    public void fieldAssignmentWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(exampleAssignment.getMetaData()).not().toBe(equalTo(null));
        expect(new FieldAssignmentImpl(fieldReference, expression, metaData).getMetaData()).toBe(metaData);
    }

}
