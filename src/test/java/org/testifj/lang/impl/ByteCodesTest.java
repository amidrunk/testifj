package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.DecompilationContext;
import org.testifj.lang.LocalVariable;
import org.testifj.lang.Method;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.impl.ConstantExpressionImpl;
import org.testifj.lang.model.impl.FieldReferenceImpl;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;
import org.testifj.lang.model.impl.VariableAssignmentImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ByteCodesTest {

    private final List<Class<?>> primitives = Arrays.asList(
            byte.class, short.class, char.class,  int.class,
            long.class, float.class, double.class, boolean.class);

    private final DecompilationContext dc = mock(DecompilationContext.class);

    private final Method method = mock(Method.class);

    @Test
    public void loadVariableShouldPushVariableReferenceOntoStack() {
        expectLocalVariable(getLocalVariable("myVariable", String.class, 1));

        ByteCodes.loadVariable(dc, method, 1);

        verify(dc).push(eq(new LocalVariableReferenceImpl("myVariable", String.class, 1)));
    }

    @Test
    public void storeVariableShouldPushAssignmentToStack() {
        final ConstantExpressionImpl value = new ConstantExpressionImpl("aValue", String.class);

        expectLocalVariable(getLocalVariable("foo", String.class, 1));
        when(dc.pop()).thenReturn(value);

        ByteCodes.storeVariable(dc, method, 1);

        verify(dc).push(new VariableAssignmentImpl(value, "foo", String.class));
    }

    @Test
    public void getFieldShouldPushFieldReferenceToStack() {
        final ConstantExpressionImpl constant = new ConstantExpressionImpl("MyString", String.class);

        when(dc.pop()).thenReturn(constant);

        ByteCodes.getField(dc, String.class, String.class, "foo");

        verify(dc).push(new FieldReferenceImpl(constant, String.class, String.class, "foo"));
    }

    @Test
    public void getStaticShouldPushStaticFieldReferenceToStack() {
        ByteCodes.getStatic(dc, BigDecimal.class, BigDecimal.class, "ONE");

        verify(dc).push(new FieldReferenceImpl(null, BigDecimal.class, BigDecimal.class, "ONE"));
    }

    @Test
    public void getFieldForStaticShouldPushStatic() {
        ByteCodes.getField(dc, String.class, String.class, "foo", true);

        verify(dc).push(eq(new FieldReferenceImpl(null, String.class, String.class, "foo")));
    }

    @Test
    public void getFieldForNonStaticShouldPushFieldRef() {
        final Expression value = mock(Expression.class);

        when(dc.pop()).thenReturn(value);

        ByteCodes.getField(dc, String.class, String.class, "foo", false);

        verify(dc).push(new FieldReferenceImpl(value, String.class, String.class, "foo"));
    }

    @Test
    public void booleanVariableCanBeLoadedFromInt() {
        expectLocalVariable(getLocalVariable("test", boolean.class, 1));
        ByteCodes.loadVariable(dc, method, 1);

        verify(dc).push(new LocalVariableReferenceImpl("test", boolean.class, 1));
    }

    private LocalVariable getLocalVariable(String name, Class<?> type, int index) {
        final LocalVariable localVariable = mock(LocalVariable.class);

        when(localVariable.getName()).thenReturn(name);
        when(localVariable.getType()).thenReturn(type);
        when(localVariable.getIndex()).thenReturn(index);

        return localVariable;
    }

    private void expectLocalVariable(LocalVariable localVariable) {
        when(method.getLocalVariableForIndex(eq(localVariable.getIndex()))).thenReturn(localVariable);
    }

}
