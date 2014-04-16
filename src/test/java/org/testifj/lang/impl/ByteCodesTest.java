package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.ClassFileFormatException;
import org.testifj.lang.DecompilationContext;
import org.testifj.lang.LocalVariable;
import org.testifj.lang.Method;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;

public class ByteCodesTest {

    private final DecompilationContext dc = mock(DecompilationContext.class);
    private final Method method = mock(Method.class);

    @Test
    public void loadVariableShouldPushVariableReferenceOntoStack() {
        final LocalVariable localVariable = getLocalVariable("myVariable", String.class, 1);

        when(method.getLocalVariableForIndex(eq(1))).thenReturn(localVariable);

        ByteCodes.loadVariable(dc, method, 1, Object.class);

        verify(dc).push(eq(new LocalVariableReferenceImpl("myVariable", String.class, 1)));
    }

    @Test
    public void loadVariableShouldFailIfVariableTypeIsNotCorrect() {
        final LocalVariable local = mock(LocalVariable.class);

        when(local.getType()).thenReturn(Object.class);
        when(method.getLocalVariableForIndex(1)).thenReturn(local);

        expect(() -> ByteCodes.loadVariable(dc, method, 1, byte.class)).toThrow(ClassFileFormatException.class);
        expect(() -> ByteCodes.loadVariable(dc, method, 1, short.class)).toThrow(ClassFileFormatException.class);
        expect(() -> ByteCodes.loadVariable(dc, method, 1, char.class)).toThrow(ClassFileFormatException.class);
        expect(() -> ByteCodes.loadVariable(dc, method, 1, int.class)).toThrow(ClassFileFormatException.class);
        expect(() -> ByteCodes.loadVariable(dc, method, 1, float.class)).toThrow(ClassFileFormatException.class);
        expect(() -> ByteCodes.loadVariable(dc, method, 1, double.class)).toThrow(ClassFileFormatException.class);
        expect(() -> ByteCodes.loadVariable(dc, method, 1, long.class)).toThrow(ClassFileFormatException.class);
        expect(() -> ByteCodes.loadVariable(dc, method, 1, boolean.class)).toThrow(ClassFileFormatException.class);

        when(local.getType()).thenReturn(int.class);

        expect(() -> ByteCodes.loadVariable(dc, method, 1, String.class)).toThrow(ClassFileFormatException.class);
    }

    private LocalVariable getLocalVariable(String name, Class<String> type, int index) {
        final LocalVariable localVariable = mock(LocalVariable.class);

        when(localVariable.getName()).thenReturn(name);
        when(localVariable.getType()).thenReturn(type);
        when(localVariable.getIndex()).thenReturn(index);

        return localVariable;
    }

}
