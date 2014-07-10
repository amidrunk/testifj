package org.testifj.lang.model;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.matchers.core.OptionalThatIs;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.OptionalThatIs.present;

@SuppressWarnings("unchecked")
public class ModelQueryTransformationTest {

    private final ModelTransformation modelTransformation = mock(ModelTransformation.class);
    private final ModelQuery modelQuery = mock(ModelQuery.class);
    private final ModelQueryTransformation<Element, Element, Element> modelQueryTransformation = new ModelQueryTransformation(modelQuery, modelTransformation);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new ModelQueryTransformation<>(null, modelTransformation)).toThrow(AssertionError.class);
        expect(() -> new ModelQueryTransformation<>(modelQuery, null)).toThrow(AssertionError.class);
    }

    @Test
    public void applyShouldReturnIfModelQueryReturnsEmptyResult() {
        final Element source = mock(Element.class);

        when(modelQuery.from(eq(source))).thenReturn(Optional.empty());

        expect(modelQueryTransformation.apply(source)).not().toBe(present());

        verifyZeroInteractions(modelTransformation);
    }

    @Test
    public void applyShouldReturnTransformedResultIfModelQuerySucceeds() {
        final Element source = mock(Element.class);
        final Element intermediate = mock(Element.class);
        final Element result = mock(Element.class);

        when(modelQuery.from(eq(source))).thenReturn(Optional.of(intermediate));
        when(modelTransformation.apply(eq(intermediate))).thenReturn(Optional.of(result));

        expect(modelQueryTransformation.apply(source)).toBe(Optional.of(result));
    }

}