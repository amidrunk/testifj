package org.testifj;

import org.testifj.annotations.DSL;
import org.testifj.lang.ByteCodeParser;
import org.testifj.lang.Lambda;
import org.testifj.lang.Method;
import org.testifj.lang.impl.ByteCodeParserImpl;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.ConstantExpressionImpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

// TODO This should use a visitor instead
public final class CodeDescriber implements Describer<CodePointer> {

    private final ByteCodeParser byteCodeParser;

    public CodeDescriber() {
        this(new ByteCodeParserImpl());
    }

    public CodeDescriber(ByteCodeParser byteCodeParser) {
        assert byteCodeParser != null : "Byte code parser can't be null";
        this.byteCodeParser = byteCodeParser;
    }

    @Override
    public Description describe(CodePointer codePointer) {
        final StringBuilder buffer = new StringBuilder();

        append(codePointer, buffer);

        return BasicDescription.from(buffer.toString());
    }

    private void append(CodePointer codePointer, StringBuilder buffer) {
        final Element element = codePointer.getElement();

        switch (element.getElementType()) {
            case RETURN:
                append(codePointer, (Return) element, buffer);
                break;
            case RETURN_VALUE:
                append(codePointer, (ReturnValue) element, buffer);
                break;
            case CONSTANT:
                append(codePointer, (ConstantExpression) element, buffer);
                break;
            case METHOD_CALL:
                append(codePointer, (MethodCall) element, buffer);
                break;
            case VARIABLE_REFERENCE:
                append(codePointer, (LocalVariableReference) element, buffer);
                break;
            case BINARY_OPERATOR:
                append(codePointer, (BinaryOperator) element, buffer);
                break;
            case FIELD_REFERENCE:
                append(codePointer, (FieldReference) element, buffer);
                break;
            case VARIABLE_ASSIGNMENT:
                append(codePointer, (VariableAssignment) element, buffer);
                break;
            case LAMBDA:
                append(codePointer, (Lambda) element, buffer);
                break;
            default:
                throw new IllegalArgumentException("Unsupported element: " + element);
        }
    }

    private void append(CodePointer codePointer, Return returnStatement, StringBuilder buffer) {
        buffer.append("return");
    }

    private void append(CodePointer codePointer, ReturnValue returnValue, StringBuilder buffer) {
        buffer.append("return ");
        append(codePointer.forElement(returnValue.getValue()), buffer);
    }

    private void append(CodePointer codePointer, ConstantExpression constant, StringBuilder buffer) {
        if (constant.getType().equals(String.class)) {
            buffer.append("\"").append(constant.getConstant()).append("\"");
        } else {
            buffer.append(constant.getConstant());
        }
    }

    private void append(CodePointer codePointer, MethodCall methodCall, StringBuilder buffer) {
        final Expression targetInstance = methodCall.getTargetInstance();
        final List<Expression> parameters = methodCall.getParameters();

        if (targetInstance == null) {
            final Element unBoxed = unbox(methodCall);

            if (unBoxed != null) {
                append(codePointer.forElement(unBoxed), buffer);
                return;
            }

            // Don't append class prefix for DSL-calls
            if (!isDSLCall(methodCall)) {
                buffer.append(methodCall.getTargetType().getTypeName()).append(".");
            }
        } else {
            // Don't add "this"-references
            if (targetInstance.getElementType() != ElementType.VARIABLE_REFERENCE
                    || !((LocalVariableReference) targetInstance).getName().equals("this")) {
                append(codePointer.forElement(targetInstance), buffer);
                buffer.append(".");
            }
        }

        buffer.append(methodCall.getMethodName()).append("(");

        for (Iterator<Expression> i = parameters.iterator(); i.hasNext(); ) {
            append(codePointer.forElement(i.next()), buffer);

            if (i.hasNext()) {
                buffer.append(", ");
            }
        }

        buffer.append(")");
    }

    private void append(CodePointer codePointer, LocalVariableReference localVariableReference, StringBuilder buffer) {
        buffer.append(localVariableReference.getName());
    }

    private void append(CodePointer codePointer, BinaryOperator binaryOperator, StringBuilder buffer) {
        append(codePointer.forElement(binaryOperator.getLeftOperand()), buffer);

        switch (binaryOperator.getOperatorType()) {
            case PLUS:
                buffer.append(" + ");
                break;
        }

        append(codePointer.forElement(binaryOperator.getRightOperand()), buffer);
    }

    private void append(CodePointer codePointer, FieldReference fieldReference, StringBuilder buffer) {
        boolean implicitTargetInstance = false;

        if (fieldReference.getTargetInstance().get().getElementType() == ElementType.VARIABLE_REFERENCE) {
            final LocalVariableReference variableReference = (LocalVariableReference) fieldReference.getTargetInstance().get();

            if (variableReference.getName().equals("this")) {
                implicitTargetInstance = true;
            }
        }

        if (!implicitTargetInstance) {
            append(codePointer.forElement(fieldReference.getTargetInstance().get()), buffer);
            buffer.append(".");
        }

        buffer.append(fieldReference.getFieldName());
    }

    private void append(CodePointer codePointer, VariableAssignment variableAssignment, StringBuilder buffer) {
        // TODO: Check if we've described declaration of the variable before, otherwise declare it

        buffer.append(((Class) variableAssignment.getVariableType()).getSimpleName()).append(" ")
                .append(variableAssignment.getVariableName())
                .append(" = ");

        append(codePointer.forElement(variableAssignment.getValue()), buffer);
    }

    private void append(CodePointer codePointer, Lambda lambda, StringBuilder buffer) {
        final Method backingMethod = codePointer.getMethod().getClassFile().getMethods().stream()
                .filter(m -> m.getName().equals(lambda.getBackingMethodName()))
                .findFirst().get();

        final ByteCodeParser parser = new ByteCodeParserImpl();
        final Element[] lambdaMethodElements;

        try (InputStream in = backingMethod.getCode().getCode()) {
            lambdaMethodElements = parser.parse(backingMethod, in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        append(codePointer.forElement(lambdaMethodElements[0]), buffer);
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
