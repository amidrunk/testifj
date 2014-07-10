package org.testifj.lang;

import org.junit.Test;

import java.util.Collection;

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

    @Test
    public void getBoxTypeShouldNotAcceptNullArg() {
        expect(() -> Types.getBoxType(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getBoxTypeShouldFailForNonPrimitiveType() {
        expect(() -> Types.getBoxType(String.class)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void getBoxTypeShouldReturnBoxTypeForPrimitive() {
        expect(Types.getBoxType(boolean.class)).toBe(Boolean.class);
        expect(Types.getBoxType(byte.class)).toBe(Byte.class);
        expect(Types.getBoxType(short.class)).toBe(Short.class);
        expect(Types.getBoxType(char.class)).toBe(Character.class);
        expect(Types.getBoxType(int.class)).toBe(Integer.class);
        expect(Types.getBoxType(long.class)).toBe(Long.class);
        expect(Types.getBoxType(float.class)).toBe(Float.class);
        expect(Types.getBoxType(double.class)).toBe(Double.class);
    }

    @Test
    public void isValueTypeAssignableToShouldNotAcceptInvalidArguments() {
        expect(() -> Types.isValueTypePotentiallyAssignableTo(null, String.class)).toThrow(AssertionError.class);
        expect(() -> Types.isValueTypePotentiallyAssignableTo(String.class, null)).toThrow(AssertionError.class);
    }

    @Test
    public void isValueTypeAssignableToShouldBeTrueForEqualTypes() {
        expect(Types.isValueTypePotentiallyAssignableTo(boolean.class, boolean.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(byte.class, byte.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(short.class, short.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(char.class, char.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(int.class, int.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(float.class, float.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(double.class, double.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(Object.class, Object.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(String.class, String.class)).toBe(true);
    }

    @Test
    public void boxTypeShouldBeAssignableToCorrespondingPrimitive() {
        expect(Types.isValueTypePotentiallyAssignableTo(Boolean.class, boolean.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(Byte.class, byte.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(Short.class, short.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(Character.class, char.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(Integer.class, int.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(Long.class, long.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(Float.class, float.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(Double.class, double.class)).toBe(true);
    }

    @Test
    public void primitiveTypeShouldBeAssignableToCorrespondingBoxType() {
        expect(Types.isValueTypePotentiallyAssignableTo(boolean.class, Boolean.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(byte.class, Byte.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(short.class, Short.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(char.class, Character.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(int.class, Integer.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(long.class, Long.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(float.class, Float.class)).toBe(true);
        expect(Types.isValueTypePotentiallyAssignableTo(double.class, Double.class)).toBe(true);
    }

    @Test
    public void isArrayShouldNotAcceptNullType() {
        expect(() -> Types.isArray(null)).toThrow(AssertionError.class);
    }

    @Test
    public void isArrayShouldReturnFalseForNonArrayType() {
        expect(Types.isArray(String.class)).toBe(false);
        expect(Types.isArray(Collection.class)).toBe(false);
        expect(Types.isArray(boolean.class)).toBe(false);
        expect(Types.isArray(byte.class)).toBe(false);
        expect(Types.isArray(short.class)).toBe(false);
        expect(Types.isArray(char.class)).toBe(false);
        expect(Types.isArray(int.class)).toBe(false);
        expect(Types.isArray(long.class)).toBe(false);
        expect(Types.isArray(float.class)).toBe(false);
        expect(Types.isArray(double.class)).toBe(false);
    }

    @Test
    public void isArrayShouldReturnTrueForArrayType() {
        expect(Types.isArray(String[].class)).toBe(true);
        expect(Types.isArray(Collection[].class)).toBe(true);
        expect(Types.isArray(boolean[].class)).toBe(true);
        expect(Types.isArray(byte[].class)).toBe(true);
        expect(Types.isArray(short[].class)).toBe(true);
        expect(Types.isArray(char[].class)).toBe(true);
        expect(Types.isArray(int[].class)).toBe(true);
        expect(Types.isArray(long[].class)).toBe(true);
        expect(Types.isArray(float[].class)).toBe(true);
        expect(Types.isArray(double[].class)).toBe(true);
    }

}