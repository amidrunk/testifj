package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.model.ElementType;

import static org.testifj.Expect.expect;

public class LocalVariableReferenceImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new LocalVariableReferenceImpl(null, String.class, 1)).toThrow(AssertionError.class);
        expect(() -> new LocalVariableReferenceImpl("", String.class, 1)).toThrow(AssertionError.class);
        expect(() -> new LocalVariableReferenceImpl("this", null, 1)).toThrow(AssertionError.class);
        expect(() -> new LocalVariableReferenceImpl("this", String.class, -1)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        final LocalVariableReferenceImpl thisVar = new LocalVariableReferenceImpl("this", Object.class, 1234);

        expect(thisVar.getElementType()).toBe(ElementType.VARIABLE_REFERENCE);
        expect(thisVar.getType()).toBe(Object.class);
        expect(thisVar.getName()).toBe("this");
        expect(thisVar.getIndex()).toBe(1234);
    }
}
