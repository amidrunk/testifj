package org.testifj.framework;

import io.recode.model.MethodCall;
import org.junit.Test;
import org.testifj.matchers.core.IntegerThatIs;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DescriptionsTest {

    @Test
    public void methodNamesCanBeConvertedToNaturalLanguage() {
        assertEquals("not", Descriptions.methodNameToNaturalLanguage("not"));
        assertEquals("to equal", Descriptions.methodNameToNaturalLanguage("toEqual"));
        assertEquals("to have the same size as", Descriptions.methodNameToNaturalLanguage("toHaveTheSameSizeAs"));
    }

    @Test
    public void isDSLMethodCallShouldOnlyReturnTrueIfTheTypeHasDSLAnnotation() {
        final MethodCall methodCall = mock(MethodCall.class);

        when(methodCall.getTargetType()).thenReturn(String.class);
        assertFalse(Descriptions.isDSLMethodCall(methodCall));

        when(methodCall.getTargetType()).thenReturn(IntegerThatIs.class);
        assertTrue(Descriptions.isDSLMethodCall(methodCall));
    }

    @Test
    public void methodCallToUnsupportedTypeShouldNotBeDSLCall() {
        final MethodCall methodCall = mock(MethodCall.class);
        final Type targetType = mock(Type.class);

        when(methodCall.getTargetType()).thenReturn(targetType);

        assertFalse(Descriptions.isDSLMethodCall(methodCall));
    }
}