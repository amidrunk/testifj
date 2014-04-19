package org.testifj.lang.impl;

import org.testifj.lang.*;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.LocalVariableReference;
import org.testifj.lang.model.Signature;
import org.testifj.lang.model.impl.MethodSignature;

import java.util.Arrays;
import java.util.Optional;

public final class InvokeDynamicExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";
        configurationBuilder.extend(ByteCode.invokedynamic, invokedynamic());
    }

    public static DecompilerExtension invokedynamic() {
        return (context, stream, byteCode) -> {
            final ClassFile classFile = context.getMethod().getClassFile();
            final ConstantPool constantPool = classFile.getConstantPool();
            final InvokeDynamicDescriptor invokeDynamicDescriptor = constantPool.getInvokeDynamicDescriptor(stream.nextUnsignedShort());
            final BootstrapMethod bootstrapMethod = getBootstrapMethod(classFile, invokeDynamicDescriptor.getBootstrapMethodAttributeIndex());
            final ConstantPoolEntryDescriptor[] descriptors = resolveValidBootstrapArguments(constantPool, bootstrapMethod);
            final MethodHandleDescriptor backingMethodHandle = descriptors[1].as(MethodHandleDescriptor.class);
            final Signature functionalMethodSignature = MethodSignature.parse(descriptors[0].as(MethodTypeDescriptor.class).getDescriptor());
            final Signature parameterizedMethodSignature = MethodSignature.parse(descriptors[2].as(MethodTypeDescriptor.class).getDescriptor());

            final Optional<Expression> self;

            boolean canHaveEnclosedVariables = true;

            switch (backingMethodHandle.getReferenceKind()) {
                case INVOKE_SPECIAL:
                    self = Optional.of(context.pop());
                    break;
                case INVOKE_VIRTUAL:
                    canHaveEnclosedVariables = false;
                default:
                    self = Optional.empty();
                    break;
            }


            // ENCLOSED VARIABLES
            final MethodSignature backingMethodSignature = MethodSignature.parse(backingMethodHandle.getMethodDescriptor());
            final LocalVariableReference[] enclosedVariables;

            if (!canHaveEnclosedVariables) {
                enclosedVariables = new LocalVariableReference[0];
            } else {
                enclosedVariables = new LocalVariableReference[backingMethodSignature.getParameterTypes().size() - functionalMethodSignature.getParameterTypes().size()];

                for (int i = enclosedVariables.length - 1; i >= 0; i--) {
                    enclosedVariables[i] = (LocalVariableReference) context.pop();
                }
            }

            backingMethodSignature.getParameterTypes();

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
