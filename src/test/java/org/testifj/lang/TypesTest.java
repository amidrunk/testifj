package org.testifj.lang;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;

public class TypesTest {

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