package org.testifj.lang.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.LocalVariable;
import org.testifj.lang.LocalVariableTable;

import java.io.IOException;

import static org.testifj.Expect.expect;

public class LocalVariableTableImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new LocalVariableTableImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() throws IOException {
        final LocalVariable localVariable = Mockito.mock(LocalVariable.class);
        final LocalVariableTableImpl localVariableTable = new LocalVariableTableImpl(new LocalVariable[]{localVariable});

        expect(localVariableTable.getLocalVariables().toArray()).toBe(new Object[]{localVariable});
        expect(localVariableTable.getName()).toBe(LocalVariableTable.ATTRIBUTE_NAME);
    }

}
