package org.testifj;

import org.testifj.lang.ByteCodeParser;
import org.testifj.lang.Method;
import org.testifj.lang.impl.ByteCodeParserImpl;
import org.testifj.lang.model.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public final class MethodBodyDescriber implements Describer<Method> {

    private final ByteCodeParser byteCodeParser;

    public MethodBodyDescriber() {
        this(new ByteCodeParserImpl());
    }

    public MethodBodyDescriber(ByteCodeParser byteCodeParser) {
        this.byteCodeParser = byteCodeParser;
    }

    @Override
    public String describe(Method method) {
        assert method != null : "Method can't be null";

        final StringBuilder buffer = new StringBuilder();
        final Element[] statements;

        try {
            statements = byteCodeParser.parse(method.getClassFile(), method.getCode().getCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < statements.length; i++) {
            final Element statement = statements[i];

            if (i == statements.length - 1 && statement.getElementType() == ElementType.RETURN) {
                // Ignore final void-return
                continue;
            }

            append(statement, buffer);
            buffer.append(";");
        }

        return buffer.toString();
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

        // Don't add "this"-references
        if (targetInstance.getElementType() != ElementType.VARIABLE_REFERENCE
                || !((LocalVariableReference) targetInstance).getVariableName().equals("this")) {
            append(targetInstance, buffer);
            buffer.append(".");
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

}
