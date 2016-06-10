package org.testifj.integrationtest;

import org.junit.Ignore;
import org.junit.Test;
import io.recode.Caller;

import java.io.PrintStream;

import static org.testifj.Expect.expect;

public class FieldRegenerationTest extends TestOnDefaultConfiguration {

    private final ExampleClass exampleClass = new ExampleClass();

    @Test
    public void staticFieldReferenceCanBeRegenerated() {
        PrintStream out = System.out;

        expect(regenerate(Caller.adjacent(-2))).toBe("PrintStream out = System.out");
    }

    @Test
    public void publicInstanceFieldReferenceCanBeRegenerated() {
        int publicIntField = exampleClass.publicIntField;

        expect(regenerate(Caller.adjacent(-2))).toBe("int publicIntField = exampleClass.publicIntField");
    }

    @Test
    public void protectedInstanceFieldReferenceCanBeRegenerated() {
        int protectedIntField = exampleClass.protectedIntField;

        expect(regenerate(Caller.adjacent(-2))).toBe("int protectedIntField = exampleClass.protectedIntField");
    }

    @Test
    public void privateInstanceFieldReferenceCanBeRegenerated() {
        int privateIntField = exampleClass.privateIntField;

        expect(regenerate(Caller.adjacent(-2))).toBe("int privateIntField = exampleClass.privateIntField");
    }

    @Test
    @Ignore("Fix when code generator has been improved")
    public void publicInstanceFieldAssignmentCanBeRegenerated() {
        exampleClass.publicIntField = 1234;

        expect(regenerate(Caller.adjacent(-2))).toBe("exampleClass.publicIntField = 1234");
    }

    public static final class ExampleClass {

        public int publicIntField;

        protected int protectedIntField;

        private int privateIntField;

    }

}
