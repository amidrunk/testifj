package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.Caller;

import static org.testifj.Expect.expect;

public class PrimitiveTypeCastRegenerationTest extends TestOnDefaultConfiguration {

    @Test
    public void integerToByteCanCastCanBeRegenerated() {
        int n = 100;
        byte b = (byte) n;

        expect(regenerate(Caller.adjacent(-2))).toBe("byte b = (byte)n");
    }

    @Test
    public void integerToCharCastCanBeRegenerated() {
        int n = 100;
        char c = (char) n;

        expect(regenerate(Caller.adjacent(-2))).toBe("char c = (char)n");
    }

    @Test
    public void integerToShortCastCanBeRegenerated() {
        int n = 100;
        short s = (short) n;

        expect(regenerate(Caller.adjacent(-2))).toBe("short s = (short)n");
    }

    @Test
    public void integerToLongCastCanBeRegenerated() {
        int n = 100;
        long l = n;

        expect(regenerate(Caller.adjacent(-2))).toBe("long l = (long)n"); // TODO: i2l does not require explicit cast
    }

    @Test
    public void integerToFloatCastCanBeRegenerated() {
        int n = 100;
        float f = n;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f = (float)n"); // TODO: i2d does not require explicit cast
    }

    @Test
    public void integerToDoubleCastCanBeRegenerated() {
        int n = 100;
        double d = n;

        expect(regenerate(Caller.adjacent(-2))).toBe("double d = (double)n"); // TODO: i2d does not require explicit cast
    }

    @Test
    public void longToIntCastCanBeRegenerated() {
        long l = 100;
        int n = (int) l;

        expect(regenerate(Caller.adjacent(-2))).toBe("int n = (int)l");
    }

    @Test
    public void longToFloatCastCanBeRegenerated() {
        long l = 100;
        float f = (float) l;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f = (float)l");
    }

    @Test
    public void longToDoubleCastCanBeRegenerated() {
        long l = 100;
        double d = (double) l;

        expect(regenerate(Caller.adjacent(-2))).toBe("double d = (double)l");
    }

    @Test
    public void floatToIntCastCanBeRegenerated() {
        float f = 100f;
        int n = (int) f;

        expect(regenerate(Caller.adjacent(-2))).toBe("int n = (int)f");
    }

    @Test
    public void floatToLongCastCanBeRegenerated() {
        float f = 100f;
        long l = (long) f;

        expect(regenerate(Caller.adjacent(-2))).toBe("long l = (long)f");
    }
}
