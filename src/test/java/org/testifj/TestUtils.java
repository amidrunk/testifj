package org.testifj;

import io.recode.classfile.Method;

public class TestUtils {

    public static StackTraceElement offset(StackTraceElement element, int offset) {
        return new StackTraceElement(
                element.getClassName(),
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber() + offset
        );
    }
}
