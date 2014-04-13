package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;

import static org.testifj.Expect.expect;

public class LocalVariableReferenceImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new LocalVariableReferenceImpl(null, String.class)).toThrow(AssertionError.class);
        expect(() -> new LocalVariableReferenceImpl("", String.class)).toThrow(AssertionError.class);
        expect(() -> new LocalVariableReferenceImpl("this", null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        final LocalVariableReferenceImpl thisVar = new LocalVariableReferenceImpl("this", Object.class);

        expect(thisVar.getElementType()).toBe(ElementType.VARIABLE_REFERENCE);
        expect(thisVar.getType()).toBe(Object.class);
        expect(thisVar.getVariableName()).toBe("this");
    }
}
