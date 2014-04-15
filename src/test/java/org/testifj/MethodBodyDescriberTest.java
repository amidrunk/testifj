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
        expect(descriptionOf("method1")).toBe("");
    }

    @Test
    public void returnOfConstantCanBeDescribed() {
        expect(descriptionOf("method2")).toBe("return 1234;");
    }

    @Test
    public void methodCallCanBeDescribed() {
        expect(descriptionOf("method3")).toBe("method1();");
    }

    @Test
    public void methodCallWithArgumentsCanBeDescribed() {
        expect(descriptionOf("method5")).toBe("method4(1, 2);");
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

        private void method1() {
        }

        private int method2() {
            return 1234;
        }

        private void method3() {
            method1();
        }

        private void method4(int m, int n) {
        }

        private void method5() {
            method4(1, 2);
        }

        private void methodWithFieldReferenceInThis() {
            string.toString();
        }

    }
}
