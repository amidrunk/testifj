package org.testifj.lang;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;

public class TypesTest {

    @Test
    public void isPrimitiveShouldNotAcceptNullType() {
        expect(() -> Types.isPrimitive(null)).toThrow(AssertionError.class);
    }

    @Test
    public void isPrimitiveShouldReturnTrueForAllPrimitives() {
        expect(Types.isPrimitive(boolean.class)).toBe(true);
        expect(Types.isPrimitive(byte.class)).toBe(true);
        expect(Types.isPrimitive(short.class)).toBe(true);
        expect(Types.isPrimitive(char.class)).toBe(true);
        expect(Types.isPrimitive(int.class)).toBe(true);
        expect(Types.isPrimitive(long.class)).toBe(true);
        expect(Types.isPrimitive(float.class)).toBe(true);
        expect(Types.isPrimitive(double.class)).toBe(true);
    }

    @Test
    public void isPrimitiveShouldReturnFalseForNonPrimitives() {
        expect(Types.isPrimitive(Object.class)).toBe(false);
        expect(Types.isPrimitive(Integer.class)).toBe(false);
        expect(Types.isPrimitive(String.class)).toBe(false);
    }

    @Test
    public void getComputationalCategoryShouldNotAcceptNullType() {
        expect(() -> Types.getComputationalCategory(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getComputationalCategoryShouldReturn1ForNonLongOrDouble() {
        expect(Types.getComputationalCategory(boolean.class)).toBe(1);
        expect(Types.getComputationalCategory(byte.class)).toBe(1);
        expect(Types.getComputationalCategory(short.class)).toBe(1);
        expect(Types.getComputationalCategory(char.class)).toBe(1);
        expect(Types.getComputationalCategory(short.class)).toBe(1);
        expect(Types.getComputationalCategory(float.class)).toBe(1);
        expect(Types.getComputationalCategory(Object.class)).toBe(1);
    }

    @Test
    public void getComputationalCategoryShouldReturn2ForLongAndDouble() {
        expect(Types.getComputationalCategory(long.class)).toBe(2);
        expect(Types.getComputationalCategory(double.class)).toBe(2);
    }

}