package org.testifj.lang;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;

import static org.testifj.Expect.expect;

public class ByteCodeTest {

    @Test
    public void isValidShouldBeFalseForInvalidInstructions() {
        expect(ByteCode.isValid(-1)).toBe(false);
        expect(ByteCode.isValid(256)).toBe(false);
    }

    @Test
    public void isValidShouldBeTrueForValidInstructions() {
        for (int i = 0; i < 256; i++) {
            expect(ByteCode.isValid(i)).toBe(true);
        }
    }

}