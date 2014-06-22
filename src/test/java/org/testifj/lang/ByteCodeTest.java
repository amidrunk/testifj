package org.testifj.lang;

import org.junit.Ignore;
import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;

import static org.testifj.Expect.expect;
import static org.testifj.lang.classfile.ByteCode.*;
import static org.testifj.matchers.core.ArrayThatIs.arrayOf;
import static org.testifj.matchers.core.ArrayThatIs.arrayWith;

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

    @Test
    @Ignore("Make this fail and fix the error")
    public void isLoadInstructionShouldReturnTrueForLoad() {
        final int[] instructions = {
                iload,
                ByteCode.iload_0,
                ByteCode.iload_1,
                ByteCode.iload_2,
                ByteCode.iload_3
        };

        for (int instruction : instructions) {
            expect(ByteCode.isLoadInstruction(instruction)).toBe(true);
        }
    }

    @Test
    public void loadInstructionsShouldReturnAllAvailableLoadInstructions() {
        expect(ByteCode.loadInstructions()).toBe(arrayWith(iload, iload_0, iload_1, iload_2, iload_3));
        expect(ByteCode.loadInstructions()).toBe(arrayWith(fload, fload_0, fload_1, fload_2, fload_3));
        expect(ByteCode.loadInstructions()).toBe(arrayWith(dload, dload_0, dload_1, dload_2, dload_3));
        expect(ByteCode.loadInstructions()).toBe(arrayWith(lload, lload_0, lload_1, lload_2, lload_3));
        expect(ByteCode.loadInstructions()).toBe(arrayWith(aload, aload_0, aload_1, aload_2, aload_3));
    }

    @Test
    public void primitiveLoadInstructionsShouldReturnAllAvailableLoadInstructions() {
        expect(ByteCode.primitiveLoadInstructions()).toBe(arrayWith(iload, iload_0, iload_1, iload_2, iload_3));
        expect(ByteCode.primitiveLoadInstructions()).toBe(arrayWith(fload, fload_0, fload_1, fload_2, fload_3));
        expect(ByteCode.primitiveLoadInstructions()).toBe(arrayWith(dload, dload_0, dload_1, dload_2, dload_3));
        expect(ByteCode.primitiveLoadInstructions()).toBe(arrayWith(lload, lload_0, lload_1, lload_2, lload_3));
        expect(ByteCode.primitiveLoadInstructions()).not().toBe(arrayWith(aload, aload_0, aload_1, aload_2, aload_3));
    }

}