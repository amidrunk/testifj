package org.testifj;

import org.junit.Ignore;
import org.junit.Test;
import org.testifj.lang.Method;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.impl.ReturnImpl;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

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

    @Test
    public void enumReferenceCanBeDescribed() {
        final ElementType fieldReference = ElementType.FIELD_REFERENCE;

        final Description description = describer.describe(ClassModelTestUtils.codeForLineOffset(-2)[0]);

        expect(description.toString()).toBe("ElementType fieldReference = ElementType.FIELD_REFERENCE");
    }

    @Test
    @Ignore("Generics are hard")
    public void lambdaWithGenericsTypeParametersCanBeDescribed() {
        final Supplier<String> supplier = () -> "Hello World!";

        final Description description = describer.describe(ClassModelTestUtils.codeForLineOffset(-2)[0]);

        expect(description.toString()).toBe("Supplier<String> supplier = () -> \"Hello World!\"");
    }

    @Test
    public void newInstanceCanBeDescribed() {
        // describer.describe(new CodePointer(mock(Method.class), new AllocateInstanceImpl(String.class)))
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
