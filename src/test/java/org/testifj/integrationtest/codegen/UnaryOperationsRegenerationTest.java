package org.testifj.integrationtest.codegen;

import org.junit.Test;
import io.recode.Caller;

import static org.testifj.Expect.expect;

public class UnaryOperationsRegenerationTest extends TestOnDefaultConfiguration {

    @Test
    public void prefixDecrementOfByteCanBeRegenerated() {
        byte i = 0;
        byte j = --i;

        expect(regenerate(Caller.adjacent(-2))).toBe("byte j = --i");
    }

    @Test
    public void prefixIncrementOfByteCanBeRegenerated() {
        byte i = 0;
        byte j = ++i;

        expect(regenerate(Caller.adjacent(-2))).toBe("byte j = ++i");
    }

    @Test
    public void postfixDecrementOfByteCanBeRegenerated() {
        byte i = 0;
        byte j = i--;

        expect(regenerate(Caller.adjacent(-2))).toBe("byte j = i--");
    }

    @Test
    public void postfixIncrementOfByteCanBeRegenerated() {
        byte i = 0;
        byte j = i++;

        expect(regenerate(Caller.adjacent(-2))).toBe("byte j = i++");
    }

    @Test
    public void prefixDecrementOfShortCanBeRegenerated() {
        short s1 = 0;
        short s2 = --s1;

        expect(regenerate(Caller.adjacent(-2))).toBe("short s2 = --s1");
    }

    @Test
    public void prefixIncrementOfShortCanBeRegenerated() {
        short s1 = 0;
        short s2 = ++s1;

        expect(regenerate(Caller.adjacent(-2))).toBe("short s2 = ++s1");
    }

    @Test
    public void postfixDecrementOfShortCanBeRegenerated() {
        short s1 = 0;
        short s2 = s1--;

        expect(regenerate(Caller.adjacent(-2))).toBe("short s2 = s1--");
    }

    @Test
    public void postfixIncrementOfShortCanBeRegenerated() {
        short s1 = 0;
        short s2 = s1++;

        expect(regenerate(Caller.adjacent(-2))).toBe("short s2 = s1++");
    }

    @Test
    public void prefixDecrementOfCharacterCanBeRegenerated() {
        char c1 = 0;
        char c2 = --c1;

        expect(regenerate(Caller.adjacent(-2))).toBe("char c2 = --c1");
    }

    @Test
    public void prefixIncrementOfCharacterCanBeRegenerated() {
        char c1 = 0;
        char c2 = ++c1;

        expect(regenerate(Caller.adjacent(-2))).toBe("char c2 = ++c1");
    }

    @Test
    public void postfixDecrementOfCharacterCanBeRegenerated() {
        char c1 = 0;
        char c2 = c1--;

        expect(regenerate(Caller.adjacent(-2))).toBe("char c2 = c1--");
    }

    @Test
    public void postfixIncrementOfCharacterCanBeRegenerated() {
        char c1 = 0;
        char c2 = c1++;

        expect(regenerate(Caller.adjacent(-2))).toBe("char c2 = c1++");
    }

    @Test
    public void prefixDecrementOfIntegerCanBeRegenerated() {
        int i = 0;
        int j = --i;

        expect(regenerate(Caller.adjacent(-2))).toBe("int j = --i");
    }

    @Test
    public void postfixDecrementOfIntegerCanBeRegenerated() {
        int i = 0;
        int j = i--;

        expect(regenerate(Caller.adjacent(-2))).toBe("int j = i--");
    }

    @Test
    public void prefixIncrementOfIntegerCanBeRegenerated() {
        int i = 0;
        int j = ++i;

        expect(regenerate(Caller.adjacent(-2))).toBe("int j = ++i");
    }

    @Test
    public void postfixIncrementOfIntegerCanBeRegenerated() {
        int i = 0;
        int j = i++;

        expect(regenerate(Caller.adjacent(-2))).toBe("int j = i++");
    }

    @Test
    public void prefixDecrementOfFloatCanBeRegenerated() {
        float f1 = 0;
        float f2 = --f1;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f2 = --f1");
    }

    @Test
    public void prefixIncrementOfFloatCanBeRegenerated() {
        float f1 = 0;
        float f2 = ++f1;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f2 = ++f1");
    }

    @Test
    public void postfixDecrementOfFloatCanBeRegenerated() {
        float f1 = 0;
        float f2 = f1--;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f2 = f1--");
    }

    @Test
    public void postfixIncrementOfFloatCanBeRegenerated() {
        float f1 = 0;
        float f2 = f1++;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f2 = f1++");
    }

    @Test
    public void prefixDecrementOfDoubleCanBeRegenerated() {
        double d1 = 0;
        double d2 = --d1;

        expect(regenerate(Caller.adjacent(-2))).toBe("double d2 = --d1");
    }

    @Test
    public void prefixIncrementOfDoubleCanBeRegenerated() {
        double d1 = 0;
        double d2 = ++d1;

        expect(regenerate(Caller.adjacent(-2))).toBe("double d2 = ++d1");
    }

    @Test
    public void postfixDecrementOfDoubleCanBeRegenerated() {
        double d1 = 0;
        double d2 = d1--;

        expect(regenerate(Caller.adjacent(-2))).toBe("double d2 = d1--");
    }

    @Test
    public void postfixIncrementOfDoubleCanBeRegenerated() {
        double d1 = 0;
        double d2 = d1++;

        expect(regenerate(Caller.adjacent(-2))).toBe("double d2 = d1++");
    }

    @Test
    public void prefixDecrementOfLongCanBeRegenerated() {
        long l1 = 0L;
        long l2 = --l1;

        expect(regenerate(Caller.adjacent(-2))).toBe("long l2 = --l1");
    }

    @Test
    public void prefixIncrementOfLongCanBeRegenerated() {
        long l1 = 0L;
        long l2 = ++l1;

        expect(regenerate(Caller.adjacent(-2))).toBe("long l2 = ++l1");
    }

    @Test
    public void postfixDecrementOfLongCanBeRegenerated() {
        long l1 = 0L;
        long l2 = l1--;

        expect(regenerate(Caller.adjacent(-2))).toBe("long l2 = l1--");
    }

    @Test
    public void postfixIncrementOfLongCanBeRegenerated() {
        long l1 = 0L;
        long l2 = l1++;

        expect(regenerate(Caller.adjacent(-2))).toBe("long l2 = l1++");
    }
}
