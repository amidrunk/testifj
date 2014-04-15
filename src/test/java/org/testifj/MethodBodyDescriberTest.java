package org.testifj;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class MethodBodyDescriberTest {

    private final MethodBodyDescriber describer = new MethodBodyDescriber();

    @Test
    public void describeShouldNotAcceptNullMethod() {
        expect(() -> describer.describe(null)).toThrow(AssertionError.class);
    }

    @Test
    public void emptyMethodCanBeDescribed() {
        expect(descriptionOf("emptyMethod")).toBe("");
    }

    @Test
    public void returnOfConstantCanBeDescribed() {
        expect(descriptionOf("integerReturn")).toBe("return 1234;");
    }

    @Test
    public void methodCallCanBeDescribed() {
        expect(descriptionOf("delegatingMethod")).toBe("emptyMethod();");
    }

    @Test
    public void methodCallWithArgumentsCanBeDescribed() {
        expect(descriptionOf("delegatingMethodWithParametersInCall")).toBe("exampleMethodWithParameters(1, 2);");
    }

    @Test
    public void methodWithFieldReferenceInThisCanBeDescribed() {
        expect(descriptionOf("methodWithFieldReferenceInThis")).toBe("string.toString();");
    }

    private String descriptionOf(String methodName) {
        return ClassModelTestUtils.describeMethod(SampleClass.class, methodName).toString();
    }

    public static final class SampleClass {

        private final String string = new String("Hello World!");

        private void emptyMethod() {
        }

        private int integerReturn() {
            return 1234;
        }

        private void delegatingMethod() {
            emptyMethod();
        }

        private void exampleMethodWithParameters(int m, int n) {
        }

        private void delegatingMethodWithParametersInCall() {
            exampleMethodWithParameters(1, 2);
        }

        private void methodWithFieldReferenceInThis() {
            string.toString();
        }

    }
}
