package org.testifj.lang.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.LocalVariable;
import org.testifj.lang.LocalVariableTable;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.testifj.Expect.expect;

public class LocalVariableTableImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new LocalVariableTableImpl(null, new LocalVariable[0])).toThrow(AssertionError.class);
        expect(() -> new LocalVariableTableImpl(Mockito.mock(ByteBuffer.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() throws IOException {
        final LocalVariable localVariable = Mockito.mock(LocalVariable.class);
        final byte[] data = {1, 2, 3};
        final LocalVariableTableImpl localVariableTable = new LocalVariableTableImpl(ByteBuffer.wrap(data), new LocalVariable[]{localVariable});

        expect(IOUtils.toByteArray(localVariableTable.getData())).toBe(data);
        expect(localVariableTable.getLocalVariables().toArray()).toBe(new Object[]{localVariable});
        expect(localVariableTable.getName()).toBe(LocalVariableTable.ATTRIBUTE_NAME);
    }

}
