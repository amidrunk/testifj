package org.testifj.lang.decompile.impl;

import org.testifj.annotations.DSL;
import org.testifj.lang.classfile.*;
import org.testifj.lang.classfile.impl.*;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.ConstantImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.*;

public final class CodePointerCodeGenerator implements CodeGenerator<CodePointer> {

    private final Decompiler decompiler;

    private final CodeGeneratorConfiguration coreConfiguration = coreConfiguration();

    public CodePointerCodeGenerator() {
        this(new DecompilerImpl());
    }

    public CodePointerCodeGenerator(Decompiler decompiler) {
        assert decompiler != null : "Byte code parser can't be null";
        this.decompiler = decompiler;
    }

    private static CodeGeneratorConfiguration coreConfiguration() {
        final CodeGeneratorConfiguration.Builder configurationBuilder = new SimpleCodeGeneratorConfiguration.Builder();

        CoreCodeGenerationExtensions.configure(configurationBuilder);

        return configurationBuilder.build();
    }

    @Override
    public void generateCode(CodePointer instance, PrintWriter out) {
        final CodeGenerationDelegate delegate = (context, codePointer) -> append(context, codePointer, out);

        // TODO type resolver should be provided
        // TODO class file resolver should be provided
        // TODO code style should be provided
        append(new CodeGenerationContextImpl(
                delegate,
                new SimpleTypeResolver(),
                new ClassPathClassFileResolver(new ClassFileReaderImpl()),
                new DecompilerImpl(),
                new ConfigurableCodeStyle.Builder().setUseSimpleClassNames(true).setShouldOmitThis(true).build()
        ), instance, out);
    }

    @SuppressWarnings("unchecked")
    private void append(CodeGenerationContext context, CodePointer codePointer, PrintWriter out) {
        final CodeGeneratorExtension extension = coreConfiguration.getExtension(context, codePointer);

        if (extension != null) {
            extension.call(context, codePointer, out);
            return;
        }

        final Element element = codePointer.getElement();

        switch (element.getElementType()) {
            case METHOD_CALL:
                append(context, codePointer, (MethodCall) element, out);
                break;
            case BINARY_OPERATOR:
                append(context, codePointer, (BinaryOperator) element, out);
                break;
            case FIELD_REFERENCE:
                append(context, codePointer, (FieldReference) element, out);
                break;
            case VARIABLE_ASSIGNMENT:
                append(context, codePointer, (VariableAssignment) element, out);
                break;
            case LAMBDA:
                append(context, codePointer, (Lambda) element, out);
                break;
            case NEW:
                append(context, codePointer, (NewInstance) element, out);
                break;
            default:
                throw new IllegalArgumentException("Unsupported element: " + element);
        }
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, MethodCall methodCall, PrintWriter out) {
        if (methodCall.getMethodName().startsWith("access$")) {
            final Expression instance = methodCall.getParameters().get(0);
            final ClassFile classFile;

            try (InputStream in = getClass().getResourceAsStream('/' + instance.getType().getTypeName().replace('.', '/') + ".class")) {
                classFile = new ClassFileReaderImpl().read(in);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final Method accessMethod = classFile.getMethods().stream().filter(m -> m.getName().equals(methodCall.getMethodName())).findFirst().get();

            try (CodeStream code = new InputStreamCodeStream(accessMethod.getCode().getCode())) {
                final Element[] elements = decompiler.parse(accessMethod, code);
                final FieldReference fieldReference = (FieldReference) ((ReturnValue) elements[0]).getValue();

                append(context, codePointer.forElement(instance), out);
                out.append(".");
                out.append(fieldReference.getFieldName());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        final Expression targetInstance = methodCall.getTargetInstance();
        final List<Expression> parameters = methodCall.getParameters();

        if (targetInstance == null) {
            final Element unBoxed = unbox(methodCall);

            if (unBoxed != null) {
                append(context, codePointer.forElement(unBoxed), out);
                return;
            }

            // Don't append class prefix for DSL-calls
            if (!isDSLCall(methodCall)) {
                out.append(simpleTypeName(methodCall.getTargetType())).append(".");
            }
        } else {
            // Don't add "this"-references
            if (targetInstance.getElementType() != ElementType.VARIABLE_REFERENCE
                    || !((LocalVariableReference) targetInstance).getName().equals("this")) {
                append(context, codePointer.forElement(targetInstance), out);
                out.append(".");
            }
        }

        out.append(methodCall.getMethodName()).append("(");

        for (Iterator<Expression> i = parameters.iterator(); i.hasNext(); ) {
            append(context, codePointer.forElement(i.next()), out);

            if (i.hasNext()) {
                out.append(", ");
            }
        }

        out.append(")");
    }

    private String simpleTypeName(Type type) {
        final String typeName = type.getTypeName();
        final int n = typeName.lastIndexOf('.');

        if (n != -1) {
            return typeName.substring(n + 1);
        }

        return typeName;
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, BinaryOperator binaryOperator, PrintWriter out) {
        append(context, codePointer.forElement(binaryOperator.getLeftOperand()), out);

        switch (binaryOperator.getOperatorType()) {
            case PLUS:
                out.append(" + ");
                break;
            case MINUS:
                out.append(" - ");
                break;
            case MULTIPLY:
                out.append(" * ");
                break;
            case DIVIDE:
                out.append(" / ");
                break;
            case EQ:
                out.append(" == ");
                break;
            case NE:
                out.append(" != ");
                break;
        }

        append(context, codePointer.forElement(binaryOperator.getRightOperand()), out);
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, FieldReference fieldReference, PrintWriter out) {
        boolean implicitTargetInstance = false;

        if (!fieldReference.getTargetInstance().isPresent()) {
            out.append(simpleTypeName(fieldReference.getDeclaringType())).append(".").append(fieldReference.getFieldName());
        } else {
            if (fieldReference.getTargetInstance().get().getElementType() == ElementType.VARIABLE_REFERENCE) {
                final LocalVariableReference variableReference = (LocalVariableReference) fieldReference.getTargetInstance().get();

                if (variableReference.getName().equals("this")) {
                    implicitTargetInstance = true;
                }
            }

            if (!implicitTargetInstance) {
                append(context, codePointer.forElement(fieldReference.getTargetInstance().get()), out);
                out.append(".");
            }

            out.append(fieldReference.getFieldName());
        }
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, VariableAssignment variableAssignment, PrintWriter out) {
        // TODO: Check if we've described declaration of the variable before, otherwise declare it

        out.append(((Class) variableAssignment.getVariableType()).getSimpleName()).append(" ")
                .append(variableAssignment.getVariableName())
                .append(" = ");

        append(context, codePointer.forElement(variableAssignment.getValue()), out);
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, Lambda lambda, PrintWriter out) {
        switch (lambda.getReferenceKind()) {
            case INVOKE_SPECIAL: {
                final Expression self = lambda.getSelf().get();

                if (!lambda.getBackingMethodName().startsWith("lambda$")) {
                    append(context, codePointer.forElement(self), out);
                    out.append("::");
                    out.append(lambda.getBackingMethodName());

                    return;
                }

                break;
            }

            case INVOKE_VIRTUAL: {
                if (lambda.getSelf().isPresent()) {
                    context.delegate(codePointer.forElement(lambda.getSelf().get()));
                } else {
                    out.append(simpleTypeName(lambda.getDeclaringClass()));
                }

                out.append("::").append(lambda.getBackingMethodName());
                return;
            }
            case INVOKE_INTERFACE: {
                // TODO Can be instance?
                out.append(simpleTypeName(lambda.getDeclaringClass()))
                        .append("::")
                        .append(lambda.getBackingMethodName());
                return;
            }
            case INVOKE_STATIC:
                if (!lambda.getBackingMethodName().startsWith("lambda$")) {
                    out.append(simpleTypeName(lambda.getDeclaringClass()))
                            .append("::")
                            .append(lambda.getBackingMethodName());
                    return;
                }
        }

        Method backingMethod = codePointer.getMethod().getClassFile().getMethods().stream()
                .filter(m -> m.getName().equals(lambda.getBackingMethodName()))
                .findFirst().get();

        final List<LocalVariableReference> enclosedVariables = lambda.getEnclosedVariables();

        if (!enclosedVariables.isEmpty()) {
            final Optional<LocalVariableTable> localVariableTable = backingMethod.getLocalVariableTable();
            final List<LocalVariable> lambdaLocals;

            if (!localVariableTable.isPresent()) {
                lambdaLocals = new ArrayList<>(lambda.getEnclosedVariables().size());
            } else {
                lambdaLocals = new LinkedList<>(localVariableTable.get().getLocalVariables());
            }

            int index = (lambda.getSelf().isPresent() ? 1 : 0);

            for (LocalVariableReference localVariableReference : enclosedVariables) {
                lambdaLocals.add(new LocalVariableImpl(-1, -1, localVariableReference.getName(),
                        localVariableReference.getType(), index));

                index++;
            }

            Collections.sort(lambdaLocals, (v1, v2) -> v1.getIndex() - v2.getIndex());

            backingMethod = backingMethod.withLocalVariableTable(new LocalVariableTableImpl(lambdaLocals.stream().toArray(LocalVariable[]::new)));
        }

        final Decompiler parser = new DecompilerImpl();
        final Element[] lambdaMethodElements;

        try (CodeStream code = new InputStreamCodeStream(backingMethod.getCode().getCode())) {
            lambdaMethodElements = parser.parse(backingMethod, code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (lambdaMethodElements.length == 1) {
            final Element lambdaMethodElement = lambdaMethodElements[0];

            if (lambdaMethodElement.getElementType() == ElementType.RETURN_VALUE) {
                final ReturnValue returnValue = (ReturnValue) lambdaMethodElement;

                if (backingMethod.getSignature().getParameterTypes().isEmpty()) {
                    out.append("() -> ");
                } else if (backingMethod.getSignature().getParameterTypes().size() == 1) {
                    final int parameterIndex;

                    if (lambda.getReferenceKind() == ReferenceKind.INVOKE_SPECIAL) {
                        parameterIndex = 1;
                    } else {
                        parameterIndex = 0;
                    }
                    final LocalVariable parameter = backingMethod.getLocalVariableTable().get().getLocalVariables().get(parameterIndex);
                    out.append(parameter.getName()).append(" -> ");
                }

                append(context, codePointer.forElement(returnValue.getValue()), out);
            } else {
                append(context, codePointer.forElement(lambdaMethodElement), out);
            }
        } else {
            if (lambdaMethodElements.length == 2 && lambdaMethodElements[1].getElementType() == ElementType.RETURN) {
                out.append("() -> ");
                append(context, codePointer.forElement(lambdaMethodElements[0]), out);
            } else {
                out.append("() -> {\n");

                final CodeGenerationContext subContext = context.subSection();

                for (int i = 0; i < lambdaMethodElements.length; i++) {
                    final Element lambdaMethodElement = lambdaMethodElements[i];

                    if (i == lambdaMethodElements.length - 1 && lambdaMethodElement.getElementType() == ElementType.RETURN) {
                        break;
                    }

                    append(context, codePointer.forElement(lambdaMethodElement), indent(subContext, out));
                    out.append(";\n");
                }
            }
        }
    }

    private void append(CodeGenerationContext context, CodePointer codePointer, NewInstance newInstance, PrintWriter out) {
        out.append("new ").append(simpleTypeName(newInstance.getType())).append("(");

        for (Iterator<Expression> i =newInstance.getParameters().iterator(); i.hasNext(); ) {
            append(context, codePointer.forElement(i.next()), out);

            if (i.hasNext()) {
                out.append(", ");
            }
        }

        out.append(")");
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

    private PrintWriter indent(CodeGenerationContext context, PrintWriter out) {
        for (int i = 0; i < context.getIndentationLevel(); i++) {
            out.append("   ");
        }

        return out;
    }
}
