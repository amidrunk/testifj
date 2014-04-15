package org.testifj;

import org.testifj.lang.ClassFile;
import org.testifj.lang.ClassFileReader;
import org.testifj.lang.Method;
import org.testifj.lang.impl.ClassFileReaderImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

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

}
