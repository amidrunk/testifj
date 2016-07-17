package org.testifj.framework;

import io.recode.annotations.DSL;
import io.recode.model.MethodCall;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public class Descriptions {

    public static String methodNameToNaturalLanguage(String methodName) {
        assert methodName != null : "methodName can't be null";

        final StringBuilder buffer = new StringBuilder(methodName.length() + 3);

        for (int i = 0; i < methodName.length(); i++) {
            final char c = methodName.charAt(i);

            if (Character.isUpperCase(c)) {
                buffer.append(' ').append(Character.toLowerCase(c));
            } else {
                buffer.append(c);
            }
        }

        return buffer.toString();
    }

    public static boolean isDSLMethodCall(MethodCall methodCall) {
        assert methodCall != null : "methodCall can't be null";

        final Type targetType = methodCall.getTargetType();

        if (!(targetType instanceof AnnotatedElement)) {
            return false;
        }

        final AnnotatedElement targetClass = (AnnotatedElement) targetType;

        return targetClass.getAnnotation(DSL.class) != null;
    }
}
