package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.ElementMetaData;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class FieldReferenceImplTest {

    private final Expression exampleTargetInstance = mock(Expression.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new FieldReferenceImpl(exampleTargetInstance, null, getClass(), "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldReferenceImpl(exampleTargetInstance, getClass(), null, "foo")).toThrow(AssertionError.class);
        expect(() -> new FieldReferenceImpl(exampleTargetInstance, getClass(), getClass(), null)).toThrow(AssertionError.class);
        expect(() -> new FieldReferenceImpl(exampleTargetInstance, getClass(), getClass(), "")).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArgumentsAndInitializeInstance() {
        final FieldReferenceImpl fieldReference = new FieldReferenceImpl(exampleTargetInstance, String.class, int.class, "foo");

        expect(fieldReference.getTargetInstance()).toBe(present());
        expect(fieldReference.getTargetInstance().get()).toBe(exampleTargetInstance);
        expect(fieldReference.getDeclaringType()).toBe(String.class);
        expect(fieldReference.getElementType()).toBe(ElementType.FIELD_REFERENCE);
        expect(fieldReference.getFieldName()).toBe("foo");
        expect(fieldReference.getFieldType()).toBe(int.class);
        expect(fieldReference.getType()).toBe(int.class);
    }

    @Test
    public void staticFieldReferenceCanBeCreated() {
        final FieldReferenceImpl ref = new FieldReferenceImpl(null, BigDecimal.class, BigDecimal.class, "ZERO");

        expect(ref.getTargetInstance()).not().toBe(present());
        expect(ref.isStatic()).toBe(true);
    }

    @Test
    public void fieldReferenceWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(new FieldReferenceImpl(exampleTargetInstance, String.class, String.class, "foo").getMetaData()).not().toBe(equalTo(null));
        expect(new FieldReferenceImpl(exampleTargetInstance, String.class, String.class, "foo", metaData).getMetaData()).toBe(metaData);
    }

}
