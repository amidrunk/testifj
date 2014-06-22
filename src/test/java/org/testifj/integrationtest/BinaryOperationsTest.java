package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.Caller;

import static org.junit.Assert.assertEquals;
import static org.testifj.Expect.expect;

public class BinaryOperationsTest extends TestOnDefaultConfiguration {

    @Test
    public void additionOfIntegersCanBeRegenerated() {
        int op1 = 1;
        int op2 = 2;
        int n = op1 + op2;

        expect(regenerate(Caller.adjacent(-2))).toBe("int n = op1 + op2");
    }

    @Test
    public void subtractionOfIntegersCanBeRegenerated() {
        int op1 = 1;
        int op2 = 2;
        int n = op1 - op2;

        expect(regenerate(Caller.adjacent(-2))).toBe("int n = op1 - op2");
    }

    @Test
    public void multiplicationOfIntegersCanBeRegenerated() {
        int op1 = 1;
        int op2 = 2;
        int n = op1 * op2;

        expect(regenerate(Caller.adjacent(-2))).toBe("int n = op1 * op2");
    }

    @Test
    public void divisionOfIntegersCanBeRegenerated() {
        int op1 = 1;
        int op2 = 2;
        int n = op1 / op2;

        expect(regenerate(Caller.adjacent(-2))).toBe("int n = op1 / op2");
    }

    @Test
    public void additionOfFloatsCanBeRegenerated() {
        float f1 = Float.valueOf(1f).floatValue();
        float f2 = Float.valueOf(2f).floatValue();
        float f3 = f1 + f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f3 = f1 + f2");
    }

    @Test
    public void subtractionOfFloatsCanBeRegenerated() {
        float f1 = Float.valueOf(1f).floatValue();
        float f2 = Float.valueOf(2f).floatValue();
        float f3 = f1 - f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f3 = f1 - f2");
    }

    @Test
    public void multiplicationOfFloatsCanBeRegenerated() {
        float f1 = Float.valueOf(1f).floatValue();
        float f2 = Float.valueOf(2f).floatValue();
        float f3 = f1 * f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f3 = f1 * f2");
    }

    @Test
    public void divisionOfFloatsCanBeRegenerated() {
        float f1 = Float.valueOf(1f).floatValue();
        float f2 = Float.valueOf(2f).floatValue();
        float f3 = f1 / f2;

        expect(regenerate(Caller.adjacent(-2))).toBe("float f3 = f1 / f2");
    }

}
