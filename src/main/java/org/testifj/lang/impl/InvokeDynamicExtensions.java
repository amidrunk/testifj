package org.testifj.lang.impl;

import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.MethodSignature;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public final class InvokeDynamicExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.invokedynamic).then(invokedynamic());

        configurationBuilder.enhance(ByteCode.invokedynamic, (context, codeStream, byteCode) -> {
            // Ok, this is like really weird. There's no way to determine that a lambda method reference
            // is an instance method reference as opposed to a static method reference. However, the compiler
            // will insert a <targetInstance>.getClass() prior to the method call and immediately discard the
            // result. Presumably, this has to do with the loading of the class or similar.

            final Lambda lambda = context.peek().as(Lambda.class);

            if (lambda.getReferenceKind() == ReferenceKind.INVOKE_VIRTUAL) {
                final List<Statement> statements = context.getStatements();

                if (!statements.isEmpty()) {
                    final Statement statement = statements.get(statements.size() - 1);

                    if (statement.getElementType() == ElementType.METHOD_CALL) {
                        final MethodCall methodCall = statement.as(MethodCall.class);
                        if (Object.class.equals(methodCall.getTargetType()) && methodCall.getMethodName().equals("getClass")) {
                            context.removeStatement(statements.size() - 1);
                        }
                    }
                }
            }
        });
    }

    public static DecompilerExtension invokedynamic() {
        return (context, stream, byteCode) -> {
            final ClassFile classFile = context.getMethod().getClassFile();
            final ConstantPool constantPool = classFile.getConstantPool();
            final InvokeDynamicDescriptor invokeDynamicDescriptor = constantPool.getInvokeDynamicDescriptor(stream.nextUnsignedShort());

            final BootstrapMethod bootstrapMethod = getBootstrapMethod(classFile, invokeDynamicDescriptor.getBootstrapMethodAttributeIndex());
            final MethodHandleDescriptor bootstrapMethodHandle = constantPool.getMethodHandleDescriptor(bootstrapMethod.getBootstrapMethodRef());

            final ConstantPoolEntryDescriptor[] descriptors = resolveValidBootstrapArguments(constantPool, bootstrapMethod);
            final MethodHandleDescriptor backingMethodHandle = descriptors[1].as(MethodHandleDescriptor.class);
            final Signature functionalMethodSignature = MethodSignature.parse(descriptors[0].as(MethodTypeDescriptor.class).getDescriptor());
            final Signature parameterizedMethodSignature = MethodSignature.parse(descriptors[2].as(MethodTypeDescriptor.class).getDescriptor());


            final Optional<Expression> self;
            final MethodSignature backingMethodSignature = MethodSignature.parse(backingMethodHandle.getMethodDescriptor());
            final LocalVariableReference[] enclosedVariables = new LocalVariableReference[Math.max(0, backingMethodSignature.getParameterTypes().size() - functionalMethodSignature.getParameterTypes().size())];

            for (int i = enclosedVariables.length - 1; i >= 0; i--) {
                enclosedVariables[i] = (LocalVariableReference) context.pop();
            }

            final MethodSignature dynamicInvokeDescriptor = MethodSignature.parse(invokeDynamicDescriptor.getMethodDescriptor());
            final Stack<Expression> dynamicCallStack = new Stack<>();
            final int dynamicCallStackSize;

            if (backingMethodHandle.getReferenceKind() == ReferenceKind.INVOKE_SPECIAL) {
                dynamicCallStackSize = 1;
            } else {
                dynamicCallStackSize = dynamicInvokeDescriptor.getParameterTypes().size() - backingMethodSignature.getParameterTypes().size();
            }

            for (int i = 0; i < dynamicCallStackSize; i++) {
                final Expression callValue = context.pop();

                if (!dynamicInvokeDescriptor.getParameterTypes().get(i).equals(callValue.getType())) {
                    throw new ClassFileFormatException("");
                }

                dynamicCallStack.push(callValue);
            }

            if (dynamicCallStack.size() > 1) {
                throw new ClassFileFormatException("Weird");
            } else if (dynamicCallStack.isEmpty()) {
                self = Optional.empty();
            } else {
                self = Optional.of(dynamicCallStack.pop());
            }

            context.push(new LambdaImpl(
                    self,
                    backingMethodHandle.getReferenceKind(),
                    MethodSignature.parse(invokeDynamicDescriptor.getMethodDescriptor()).getReturnType(),
                    invokeDynamicDescriptor.getMethodName(),
                    functionalMethodSignature,
                    context.resolveType(backingMethodHandle.getClassName()),
                    backingMethodHandle.getMethodName(),
                    backingMethodSignature,
                    Arrays.asList(enclosedVariables)
            ));

            return true;
        };
    }

    private static ConstantPoolEntryDescriptor[] resolveValidBootstrapArguments(ConstantPool constantPool, BootstrapMethod bootstrapMethod) {
        final ConstantPoolEntryDescriptor[] descriptors = constantPool.getDescriptors(bootstrapMethod.getBootstrapArguments());

        if (descriptors[0].getTag() != ConstantPoolEntryTag.METHOD_TYPE) {
            throw new UnsupportedOperationException();
        }

        if (descriptors[1].getTag() != ConstantPoolEntryTag.METHOD_HANDLE) {
            throw new UnsupportedOperationException();
        }

        if (descriptors[2].getTag() != ConstantPoolEntryTag.METHOD_TYPE) {
            throw new UnsupportedOperationException();
        }
        return descriptors;
    }

    private static BootstrapMethod getBootstrapMethod(ClassFile classFile, int bootstrapMethodAttributeIndex) {
        return classFile.getBootstrapMethodsAttribute()
                .orElseThrow(() -> new ClassFileFormatException("No bootstrap methods attribute is available in class " + classFile.getName()))
                .getBootstrapMethods()
                .get(bootstrapMethodAttributeIndex);
    }

}
