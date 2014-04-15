package org.testifj;

import org.testifj.annotations.DSL;
import org.testifj.lang.Lambda;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.ConstantExpressionImpl;

import java.lang.reflect.Type;
import java.util.*;

public final class MethodElementDescriber implements Describer<Element> {

    @Override
    public Description describe(Element element) {
        final StringBuilder buffer = new StringBuilder();

        append(element, buffer);

        return BasicDescription.from(buffer.toString());
    }

    private void append(Element element, StringBuilder buffer) {
        switch (element.getElementType()) {
            case RETURN:
                append((Return) element, buffer);
                break;
            case RETURN_VALUE:
                append((ReturnValue) element, buffer);
                break;
            case CONSTANT:
                append((ConstantExpression) element, buffer);
                break;
            case METHOD_CALL:
                append((MethodCall) element, buffer);
                break;
            case VARIABLE_REFERENCE:
                append((LocalVariableReference) element, buffer);
                break;
            case BINARY_OPERATOR:
                append((BinaryOperator) element, buffer);
                break;
            case FIELD_REFERENCE:
                append((FieldReference) element, buffer);
                break;
            case LAMBDA:
                append((Lambda) element, buffer);
                break;
            default:
                throw new IllegalArgumentException("Unsupported element: " + element);
        }
    }

    private void append(Return returnStatement, StringBuilder buffer) {
        buffer.append("return");
    }

    private void append(ReturnValue returnValue, StringBuilder buffer) {
        buffer.append("return ");
        append(returnValue.getValue(), buffer);
    }

    private void append(ConstantExpression constant, StringBuilder buffer) {
        if (constant.getType().equals(String.class)) {
            buffer.append("\"").append(constant.getConstant()).append("\"");
        } else {
            buffer.append(constant.getConstant());
        }
    }

    private void append(MethodCall methodCall, StringBuilder buffer) {
        final Expression targetInstance = methodCall.getTargetInstance();
        final List<Expression> parameters = methodCall.getParameters();

        if (targetInstance == null) {
            final Element unBoxed = unbox(methodCall);

            if (unBoxed != null) {
                append(unBoxed, buffer);
                return;
            }

            // Don't append class prefix for DSL-calls
            if (!isDSLCall(methodCall)) {
                buffer.append(methodCall.getTargetType().getTypeName()).append(".");
            }
        } else {
            // Don't add "this"-references
            if (targetInstance.getElementType() != ElementType.VARIABLE_REFERENCE
                    || !((LocalVariableReference) targetInstance).getVariableName().equals("this")) {
                append(targetInstance, buffer);
                buffer.append(".");
            }
        }

        buffer.append(methodCall.getMethodName()).append("(");

        for (Iterator<Expression> i = parameters.iterator(); i.hasNext(); ) {
            append(i.next(), buffer);

            if (i.hasNext()) {
                buffer.append(", ");
            }
        }

        buffer.append(")");
    }

    private void append(LocalVariableReference localVariableReference, StringBuilder buffer) {
        buffer.append(localVariableReference.getVariableName());
    }

    private void append(BinaryOperator binaryOperator, StringBuilder buffer) {
        append(binaryOperator.getLeftOperand(), buffer);

        switch (binaryOperator.getOperatorType()) {
            case PLUS:
                buffer.append(" + ");
                break;
        }

        append(binaryOperator.getRightOperand(), buffer);
    }

    private void append(FieldReference fieldReference, StringBuilder buffer) {
        boolean implicitTargetInstance = false;

        if (fieldReference.getTargetInstance().getElementType() == ElementType.VARIABLE_REFERENCE) {
            final LocalVariableReference variableReference = (LocalVariableReference) fieldReference.getTargetInstance();

            if (variableReference.getVariableName().equals("this")) {
                implicitTargetInstance = true;
            }
        }

        if (!implicitTargetInstance) {
            append(fieldReference.getTargetInstance(), buffer);
            buffer.append(".");
        }

        buffer.append(fieldReference.getFieldName());
    }

    private void append(Lambda lambda, StringBuilder buffer) {
        throw new RuntimeException("Need access to method for lambda: " + lambda);
    }

    private boolean isDSLCall(MethodCall methodCall) {
        final Type targetType = methodCall.getTargetType();

        if (!(targetType instanceof Class)) {
            return false;
        }

        final Class targetClass = (Class) targetType;

        return targetClass.getAnnotation(DSL.class) != null;
    }

    private static final Map<Class, Class> BOX_MAP = new HashMap<Class, Class>() {{
        put(Boolean.class, boolean.class);
        put(Byte.class, byte.class);
        put(Short.class, short.class);
        put(Character.class, char.class);
        put(Integer.class, int.class);
        put(Long.class, long.class);
        put(Float.class, float.class);
        put(Double.class, double.class);
    }};


    private Expression unbox(MethodCall methodCall) {
        final Type targetType = methodCall.getTargetType();
        final Type primitiveType = BOX_MAP.get(targetType);

        if (primitiveType == null) {
            return null;
        }

        final List<Expression> parameters = methodCall.getParameters();

        if (targetType.equals(Boolean.class) && parameters.get(0).getType().equals(int.class) && parameters.get(0).getElementType() == ElementType.CONSTANT) {
            final ConstantExpression constant = (ConstantExpression) parameters.get(0);

            return new ConstantExpressionImpl((Integer) constant.getConstant() == 1, boolean.class);
        }

        if (methodCall.getMethodName().equals("valueOf")
                && parameters.size() == 1
                && (parameters.get(0).getType().equals(primitiveType))) {
            return parameters.get(0);
        }

        return null;
    }
}
