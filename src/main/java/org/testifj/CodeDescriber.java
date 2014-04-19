package org.testifj;

import org.testifj.annotations.DSL;
import org.testifj.lang.Decompiler;
import org.testifj.lang.Lambda;
import org.testifj.lang.LocalVariable;
import org.testifj.lang.Method;
import org.testifj.lang.impl.DecompilerImpl;
import org.testifj.lang.impl.LocalVariableImpl;
import org.testifj.lang.impl.LocalVariableTableImpl;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.ConstantImpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// TODO This should use a visitor instead
public final class CodeDescriber implements Describer<CodePointer> {

    private final Decompiler decompiler;

    public CodeDescriber() {
        this(new DecompilerImpl());
    }

    public CodeDescriber(Decompiler decompiler) {
        assert decompiler != null : "Byte code parser can't be null";
        this.decompiler = decompiler;
    }

    @Override
    public Description describe(CodePointer codePointer) {
        final StringBuilder buffer = new StringBuilder();
        final CodeGenerationContext context = new CodeGenerationContext();

        append(context, codePointer, buffer);

        return BasicDescription.from(buffer.toString());
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, StringBuilder buffer) {
        final Element element = codePointer.getElement();

        switch (element.getElementType()) {
            case RETURN:
                append(context, codePointer, (Return) element, buffer);
                break;
            case RETURN_VALUE:
                append(context, codePointer, (ReturnValue) element, buffer);
                break;
            case CONSTANT:
                append(context, codePointer, (Constant) element, buffer);
                break;
            case METHOD_CALL:
                append(context, codePointer, (MethodCall) element, buffer);
                break;
            case VARIABLE_REFERENCE:
                append(context, codePointer, (LocalVariableReference) element, buffer);
                break;
            case BINARY_OPERATOR:
                append(context, codePointer, (BinaryOperator) element, buffer);
                break;
            case FIELD_REFERENCE:
                append(context, codePointer, (FieldReference) element, buffer);
                break;
            case VARIABLE_ASSIGNMENT:
                append(context, codePointer, (VariableAssignment) element, buffer);
                break;
            case LAMBDA:
                append(context, codePointer, (Lambda) element, buffer);
                break;
            default:
                throw new IllegalArgumentException("Unsupported element: " + element);
        }
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, Return returnStatement, StringBuilder buffer) {
        buffer.append("return");
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, ReturnValue returnValue, StringBuilder buffer) {
        buffer.append("return ");
        append(context, codePointer.forElement(returnValue.getValue()), buffer);
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, Constant constant, StringBuilder buffer) {
        if (constant.getType().equals(String.class)) {
            buffer.append("\"").append(constant.getConstant()).append("\"");
        } else {
            buffer.append(constant.getConstant());
        }
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, MethodCall methodCall, StringBuilder buffer) {
        final Expression targetInstance = methodCall.getTargetInstance();
        final List<Expression> parameters = methodCall.getParameters();

        if (targetInstance == null) {
            final Element unBoxed = unbox(methodCall);

            if (unBoxed != null) {
                append(context, codePointer.forElement(unBoxed), buffer);
                return;
            }

            // Don't append class prefix for DSL-calls
            if (!isDSLCall(methodCall)) {
                buffer.append(simpleTypeName(methodCall.getTargetType())).append(".");
            }
        } else {
            // Don't add "this"-references
            if (targetInstance.getElementType() != ElementType.VARIABLE_REFERENCE
                    || !((LocalVariableReference) targetInstance).getName().equals("this")) {
                append(context, codePointer.forElement(targetInstance), buffer);
                buffer.append(".");
            }
        }

        buffer.append(methodCall.getMethodName()).append("(");

        for (Iterator<Expression> i = parameters.iterator(); i.hasNext(); ) {
            append(context, codePointer.forElement(i.next()), buffer);

            if (i.hasNext()) {
                buffer.append(", ");
            }
        }

        buffer.append(")");
    }

    private String simpleTypeName(Type type) {
        final String typeName = type.getTypeName();
        final int n = typeName.lastIndexOf('.');

        if (n != -1) {
            return typeName.substring(n + 1);
        }

        return typeName;
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, LocalVariableReference localVariableReference, StringBuilder buffer) {
        buffer.append(localVariableReference.getName());
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, BinaryOperator binaryOperator, StringBuilder buffer) {
        append(context, codePointer.forElement(binaryOperator.getLeftOperand()), buffer);

        switch (binaryOperator.getOperatorType()) {
            case PLUS:
                buffer.append(" + ");
                break;
            case EQ:
                buffer.append(" == ");
                break;
            case NE:
                buffer.append(" != ");
                break;
        }

        append(context, codePointer.forElement(binaryOperator.getRightOperand()), buffer);
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, FieldReference fieldReference, StringBuilder buffer) {
        boolean implicitTargetInstance = false;

        if (!fieldReference.getTargetInstance().isPresent()) {
            buffer.append(simpleTypeName(fieldReference.getDeclaringType())).append(".").append(fieldReference.getFieldName());
        } else {
            if (fieldReference.getTargetInstance().get().getElementType() == ElementType.VARIABLE_REFERENCE) {
                final LocalVariableReference variableReference = (LocalVariableReference) fieldReference.getTargetInstance().get();

                if (variableReference.getName().equals("this")) {
                    implicitTargetInstance = true;
                }
            }

            if (!implicitTargetInstance) {
                append(context, codePointer.forElement(fieldReference.getTargetInstance().get()), buffer);
                buffer.append(".");
            }

            buffer.append(fieldReference.getFieldName());
        }
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, VariableAssignment variableAssignment, StringBuilder buffer) {
        // TODO: Check if we've described declaration of the variable before, otherwise declare it

        buffer.append(((Class) variableAssignment.getVariableType()).getSimpleName()).append(" ")
                .append(variableAssignment.getVariableName())
                .append(" = ");

        append(context, codePointer.forElement(variableAssignment.getValue()), buffer);
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, Lambda lambda, StringBuilder buffer) {
        switch (lambda.getReferenceKind()) {
            case INVOKE_SPECIAL: {
                final Expression self = lambda.getSelf().get();

                if (!lambda.getBackingMethodName().startsWith("lambda$")) {
                    append(context, codePointer.forElement(self), buffer);
                    buffer.append("::");
                    buffer.append(lambda.getBackingMethodName());

                    return;
                }

                break;
            }

            case INVOKE_VIRTUAL: {
                buffer.append(simpleTypeName(lambda.getDeclaringClass()))
                        .append("::")
                        .append(lambda.getBackingMethodName());
                return;
            }
        }

        Method backingMethod = codePointer.getMethod().getClassFile().getMethods().stream()
                .filter(m -> m.getName().equals(lambda.getBackingMethodName()))
                .findFirst().get();

        if (!backingMethod.getLocalVariableTable().isPresent()) {
            final List<LocalVariableReference> enclosedVariables = lambda.getEnclosedVariables();
            final LocalVariable[] lambdaLocals = new LocalVariable[enclosedVariables.size()];

            int index = 0;

            for (LocalVariableReference localVariableReference : enclosedVariables) {
                lambdaLocals[index] = (new LocalVariableImpl(-1, -1, localVariableReference.getName(),
                        localVariableReference.getType(), index));

                index++;
            }

            backingMethod = backingMethod.withLocalVariableTable(new LocalVariableTableImpl(lambdaLocals));
        }

        final Decompiler parser = new DecompilerImpl();
        final Element[] lambdaMethodElements;

        try (InputStream in = backingMethod.getCode().getCode()) {
            lambdaMethodElements = parser.parse(backingMethod, in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (lambdaMethodElements.length == 1) {
            final Element lambdaMethodElement = lambdaMethodElements[0];

            if (lambdaMethodElement.getElementType() == ElementType.RETURN_VALUE) {
                final ReturnValue returnValue = (ReturnValue) lambdaMethodElement;

                if (backingMethod.getSignature().getParameterTypes().isEmpty()) {
                    buffer.append("() -> ");
                } else if (backingMethod.getSignature().getParameterTypes().size() == 1) {
                    final LocalVariable parameter = backingMethod.getLocalVariableTable().get().getLocalVariables().get(0);
                    buffer.append(parameter.getName()).append(" -> ");
                }

                append(context, codePointer.forElement(returnValue.getValue()), buffer);
            } else {
                append(context, codePointer.forElement(lambdaMethodElement), buffer);
            }
        } else {
            if (lambdaMethodElements.length == 2 && lambdaMethodElements[1].getElementType() == ElementType.RETURN) {
                buffer.append("() -> ");
                append(context, codePointer.forElement(lambdaMethodElements[0]), buffer);
            } else {
                buffer.append("() -> {\n");

                final CodeGenerationContext subContext = context.subSection();

                for (int i = 0; i < lambdaMethodElements.length; i++) {
                    final Element lambdaMethodElement = lambdaMethodElements[i];

                    if (i == lambdaMethodElements.length - 1 && lambdaMethodElement.getElementType() == ElementType.RETURN) {
                        break;
                    }

                    append(context, codePointer.forElement(lambdaMethodElement), indent(subContext, buffer));
                    buffer.append(";\n");
                }
            }
        }
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
            final Constant constant = (Constant) parameters.get(0);

            return new ConstantImpl((Integer) constant.getConstant() == 1, boolean.class);
        }

        if (methodCall.getMethodName().equals("valueOf")
                && parameters.size() == 1
                && (parameters.get(0).getType().equals(primitiveType))) {
            return parameters.get(0);
        }

        return null;
    }

    private StringBuilder indent(CodeGenerationContext context, StringBuilder buffer) {
        for (int i = 0; i < context.getIndentationLevel(); i++) {
            buffer.append("   ");
        }

        return buffer;
    }
}
