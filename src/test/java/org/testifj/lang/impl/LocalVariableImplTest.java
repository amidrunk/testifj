package org.testifj.lang.impl;

import org.junit.Test;

import java.lang.reflect.Type;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class LocalVariableImplTest {

    private final Type exampleType = mock(Type.class);

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new LocalVariableImpl(1, 2, null, exampleType, 3)).toThrow(AssertionError.class);
        expect(() -> new LocalVariableImpl(1, 2, "", exampleType, 3)).toThrow(AssertionError.class);
        expect(() -> new LocalVariableImpl(1, 2, "foo", null, 3)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() throws Exception {
        final LocalVariableImpl variableTable = new LocalVariableImpl(1, 2, "foo", String.class, 3);

        expect(variableTable.getStartPC()).toBe(1);
        expect(variableTable.getLength()).toBe(2);
        expect(variableTable.getVariableName()).toBe("foo");
        expect(variableTable.getVariableType()).toBe(String.class);
        expect(variableTable.getIndex()).toBe(3);
    }

}
