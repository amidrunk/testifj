package org.testifj.lang;

import org.testifj.Caller;
import org.testifj.Description;
import org.testifj.MethodBodyCodeGenerator;
import org.testifj.lang.impl.*;
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
        return new MethodBodyCodeGenerator().describe(method);
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
        try (CodeStream code = new InputStreamCodeStream(method.getCode().getCode())) {
            return new DecompilerImpl().parse(method, code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toCode(CodePointer codePointer) {
        return new CodePointerCodeGenerator().describe(codePointer).toString();
    }

    public static CodePointer[] code(Caller caller) {
        try {
            return new CallerDecompilerImpl().decompileCaller(caller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
