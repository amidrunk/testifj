package org.testifj;

import io.recode.classfile.ClassFile;
import io.recode.classfile.ClassFileResolver;
import io.recode.classfile.ClassPathClassFileResolver;
import io.recode.classfile.Method;
import io.recode.classfile.impl.ClassFileReaderImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class MethodBodyCodeGeneratorTest {

    private final MethodBodyCodeGenerator describer = new MethodBodyCodeGenerator();

    @Test
    public void generateCodeToStringShouldRejectNullMethodOrNullCharset() {
        expect(() -> describer.generateCode(null, StandardCharsets.UTF_8)).toThrow(AssertionError.class);
        expect(() -> describer.generateCode(mock(Method.class), (Charset) null)).toThrow(AssertionError.class);
    }

    @Test
    public void emptyMethodCanBeDescribed() {
        assertEquals("", descriptionOf("emptyMethod"));
    }

    @Test
    public void returnOfConstantCanBeDescribed() {
        assertEquals("return 1234;", descriptionOf("integerReturn"));
    }

    @Test
    public void methodCallCanBeDescribed() {
        assertEquals("emptyMethod();", descriptionOf("delegatingMethod"));
    }

    @Test
    public void methodCallWithArgumentsCanBeDescribed() {
        assertEquals("exampleMethodWithParameters(1, 2);", descriptionOf("delegatingMethodWithParametersInCall"));
    }

    @Test
    public void methodWithFieldReferenceInThisCanBeDescribed() {
        assertEquals("string.toString();", descriptionOf("methodWithFieldReferenceInThis"));
    }

    @Test
    @Ignore("Try to fix generics later")
    public void methodWithLambdaCanBeDescribed() {
        assertEquals(
                "Supplier<String> s = () -> \"foo\";\n" +
                        "s.get();"
                , descriptionOf("methodWithLambda"));
    }

    private String descriptionOf(String methodName) {
        final MethodBodyCodeGenerator codeGenerator = new MethodBodyCodeGenerator();
        final ClassFileResolver classFileResolver = new ClassPathClassFileResolver(new ClassFileReaderImpl());
        final ClassFile classFile = classFileResolver.resolveClassFile(SampleClass.class);
        final Method method = classFile.getMethods().stream()
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Method '" + methodName + "' not found in class " + classFile.getName()));

        return codeGenerator.generateCode(method, StandardCharsets.UTF_8);
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

        private void methodWithLambda() {
            Supplier<String> s = () -> "foo";

            s.get();
        }

    }
}
