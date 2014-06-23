package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.Caller;

import static org.testifj.Expect.expect;

public class BooleanOperationsTest extends TestOnDefaultConfiguration {

    private final ExampleClass exampleClass = new ExampleClass();

    @Test
    public void booleanTrueAssignmentCanBeRegenerated() {
        boolean b = true;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = true");
    }

    @Test
    public void booleanFalseAssignmentCanBeRegenerated() {
        boolean b = false;

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = false");
    }

    @Test
    public void booleanAssignmentFromMethodCallCanBeRegenerated() {
        boolean b = "hello".isEmpty();

        expect(regenerate(Caller.adjacent(-2))).toBe("boolean b = \"hello\".isEmpty()");
    }

    @Test
    public void methodCallWithConstantBooleanArgumentCanBeRegenerated() {
        exampleClass.accept(true);
        expect(regenerate(Caller.adjacent(-1))).toBe("exampleClass.accept(true)");

        exampleClass.accept(false);
        expect(regenerate(Caller.adjacent(-1))).toBe("exampleClass.accept(false)");
    }

    public static final class ExampleClass {

        public void accept(boolean b) {}

    }
}
