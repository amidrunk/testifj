package org.testifj;

import org.junit.Test;
import org.testifj.lang.ClassFile;
import org.testifj.lang.ClassFileReader;
import org.testifj.lang.Method;
import org.testifj.lang.impl.ClassFileReaderImpl;

import java.io.InputStream;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class MethodBodyDescriberTest {

    private final MethodBodyDescriber describer = new MethodBodyDescriber();

    @Test
    public void describeShouldNotAcceptNullMethod() {
        expect(() -> describer.describe(null)).toThrow(AssertionError.class);
    }

    @Test
    public void emptyMethodCanBeDescribed() {
        final String description = describe(m -> m.getName().equals("method1"));

        expect(description).to(equal(""));
    }

    @Test
    public void returnOfConstantCanBeDescribed() {
        expect(describe("method2")).to(equal("return 1234;"));
    }

    @Test
    public void methodCallCanBeDescribed() {
        expect(describe("method3")).to(equal("method1();"));
    }

    @Test
    public void methodCallWithArgumentsCanBeDescribed() {
        expect(describe("method5")).to(equal("method4(1, 2);"));
    }

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

    private String describe(String methodName) {
        return describe(m -> m.getName().equals(methodName));
    }

    private String describe(java.util.function.Predicate<Method> predicate) {
        final ClassFileReader reader = new ClassFileReaderImpl();

        try (InputStream in = getClass().getResourceAsStream("/" + getClass().getName().replace('.', '/') + ".class")) {
            final ClassFile classFile = reader.read(in);

            return describer.describe(classFile.getMethods().stream().filter(predicate).findFirst().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
