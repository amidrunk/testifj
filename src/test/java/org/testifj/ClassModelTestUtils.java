package org.testifj;

import org.testifj.lang.ByteCodeParser;
import org.testifj.lang.ClassFile;
import org.testifj.lang.ClassFileReader;
import org.testifj.lang.Method;
import org.testifj.lang.impl.ByteCodeParserImpl;
import org.testifj.lang.impl.ClassFileReaderImpl;
import org.testifj.lang.model.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

import static org.testifj.Expect.expect;

public class ClassModelTestUtils {

    public static ClassFile classFileOf(Class<?> clazz) {
        final String resourceName = "/" + clazz.getName().replace('.', '/') + ".class";

        try (InputStream in = clazz.getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new AssertionError("No class file resource exists for class '"
                        + clazz.getName() + "' (resource = " + resourceName + ")");
            }

            final ClassFileReader classFileReader = new ClassFileReaderImpl();

            return classFileReader.read(in);
        } catch (IOException e) {
            throw new AssertionError("getResourceAsStream failed with IOException", e);
        }
    }

    public static Method methodWithName(Class<?> clazz, String methodName) {
        return methodWithName(classFileOf(clazz), methodName);
    }

    public static Method methodWithName(ClassFile classFile, String methodName) {
        final Optional<Method> optionalMethod = classFile.getMethods().stream()
                .filter(m -> m.getName().equals(methodName))
                .findFirst();

        return optionalMethod.orElseThrow(() -> new AssertionError("Method not found: " + classFile.getName() + "." + methodName + "(..)"));
    }

    public static Description describeMethod(Class<?> clazz, String methodName) {
        return descriptionOf(methodWithName(clazz, methodName));
    }

    public static Description descriptionOf(Method method) {
        return new MethodBodyDescriber().describe(method);
    }

    public static StackTraceElement offset(StackTraceElement element, int offset) {
        return new StackTraceElement(
                element.getClassName(),
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber() + offset
        );
    }

    public static Element[] methodBodyOf(Class<?> clazz, String methodName) {
        return methodBodyOf(methodWithName(classFileOf(clazz), methodName));
    }

    public static Element[] methodBodyOf(Method method) {
        try (InputStream code = method.getCode().getCode()) {
            return new ByteCodeParserImpl().parse(method, code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String lineToString(int delta) {
        final CodePointer[] codePointers = codeForLineOffset(3, delta);
        expect(codePointers.length).toBe(1);
        return new CodeDescriber().describe(codePointers[0]).toString();
    }

    public static CodePointer[] codeForLineOffset(int delta) {
        return codeForLineOffset(3, delta);
    }

    public static CodePointer[] codeForLineOffset(int stackTraceIndex, int delta) {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        final StackTraceElement callerStackTraceElement = stackTraceElements[stackTraceIndex];

        final Method method;

        try {
            method = methodWithName(Class.forName(callerStackTraceElement.getClassName()), callerStackTraceElement.getMethodName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found", e);
        }

        try (InputStream in = method.getCodeForLineNumber(callerStackTraceElement.getLineNumber() + delta)) {
            return Arrays.stream(new ByteCodeParserImpl().parse(method, in))
                    .map(e -> new CodePointer(method, e))
                    .toArray(CodePointer[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
