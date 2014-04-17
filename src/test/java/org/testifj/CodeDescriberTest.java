package org.testifj;

import com.sun.tools.internal.xjc.util.CodeModelClassFactory;
import org.junit.Test;
import org.testifj.lang.Method;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.impl.ReturnImpl;

import java.util.Arrays;
import java.util.function.Function;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class CodeDescriberTest {

    private final CodeDescriber describer = new CodeDescriber();
    private final Method method = mock(Method.class);

    @Test
    public void constructorShouldNotAcceptNullCodeDescriber() {
        expect(() -> new CodeDescriber(null)).toThrow(AssertionError.class);
    }

    @Test
    public void returnCanBeDescribed() {
        final Description description = describer.describe(pointer(new ReturnImpl()));

        expect(description).toBe(BasicDescription.from("return"));
    }

    @Test
    public void lambdaMethodReferenceCanBeDescribed() {
        expect(this::lambdaTargetTest);

        final Description description = describer.describe(ClassModelTestUtils.codeForLineOffset(-2)[0]);

        expect(description.toString()).toBe("expect(this::lambdaTargetTest)");
    }

    @Test
    public void lambdaWithImplicitThisAndOtherCodeCanBeDescribed() {
        expect(() -> {
            Math.random();
            this.lambdaTargetTest();
        });

        final Description description = describer.describe(ClassModelTestUtils.codeForLineOffset(-5)[0]);

        expect(description.toString()).toBe(
            "expect(() -> {\n" +
            "   Math.random();\n" +
            "   lambdaTargetTest();\n" +
            ")"
        );
    }

    @Test
    public void lambdaWithInstanceMethodReferenceCanBeDescribed() {
        callFunction(String::length, "foo");

        final Description description = describer.describe(ClassModelTestUtils.codeForLineOffset(-2)[0]);

        expect(description.toString()).toBe("callFunction(String::length, \"foo\")");
    }

    @Test
    public void lambdaWithArgumentsAndCodeCanBeDescribed() {
        callFunction(s -> s.length() + 1, "foo");

        final Description description = describer.describe(ClassModelTestUtils.codeForLineOffset(-2)[0]);

        expect(description.toString()).toBe("callFunction(s -> s.length() + 1, \"foo\")");
    }

    private CodePointer pointer(Element element) {
        return new CodePointer(method, element);
    }

    private void lambdaTargetTest() {
    }

    private <T, R> R callFunction(Function<T, R> function, T arg) {
        return function.apply(arg);
    }

}
