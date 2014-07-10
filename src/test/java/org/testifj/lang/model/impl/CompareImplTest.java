package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.Compare;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class CompareImplTest {

    private final Expression rightOperand = mock(Expression.class);
    private final Expression leftOperand = mock(Expression.class);
    private final Compare exampleCompare = new CompareImpl(leftOperand, rightOperand);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new CompareImpl(null, rightOperand)).toThrow(AssertionError.class);
        expect(() -> new CompareImpl(leftOperand, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        expect(exampleCompare.getLeftOperand()).toBe(leftOperand);
        expect(exampleCompare.getRightOperand()).toBe(rightOperand);
        expect(exampleCompare.getElementType()).toBe(ElementType.COMPARE);
        expect(exampleCompare.getType()).toBe(int.class);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleCompare).toBe(equalTo(exampleCompare));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleCompare).not().toBe(equalTo(null));
        expect((Object) exampleCompare).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Compare other = new CompareImpl(leftOperand, rightOperand);

        expect(exampleCompare).toBe(equalTo(other));
        expect(exampleCompare.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void compareWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(exampleCompare.getMetaData()).not().toBe(equalTo(null));
        expect(new CompareImpl(leftOperand, rightOperand, metaData).getMetaData()).toBe(metaData);
    }

}