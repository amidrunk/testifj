package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.Caller;

import static org.testifj.Expect.expect;

public class UnaryOperationsTest extends TestOnDefaultConfiguration {

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

}
