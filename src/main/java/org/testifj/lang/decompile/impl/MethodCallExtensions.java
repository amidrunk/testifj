package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.classfile.InterfaceMethodRefDescriptor;
import org.testifj.lang.classfile.MethodRefDescriptor;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerExtension;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Signature;
import org.testifj.lang.model.impl.MethodCallImpl;
import org.testifj.lang.model.impl.MethodSignature;

import java.lang.reflect.Type;

public final class MethodCallExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.invokeinterface).then(invokeinterface());
        configurationBuilder.on(ByteCode.invokespecial).then(invokespecial());
    }

    public static DecompilerExtension invokeinterface() {
        return (context, code, instruction) -> {
            final InterfaceMethodRefDescriptor interfaceMethodRefDescriptor = context
                    .getMethod()
                    .getClassFile()
                    .getConstantPool()
                    .getInterfaceMethodRefDescriptor(code.nextUnsignedShort());

            invoke(context, interfaceMethodRefDescriptor);

            if (code.nextUnsignedByte() == 0) {
                throw new ClassFileFormatException("Expected byte subsequent to interface method invocation to be non-zero");
            }

            return true;
        };
    }

    public static DecompilerExtension invokespecial() {
        return (context, code, instruction) -> {
            final MethodRefDescriptor methodRefDescriptor = context
                    .getMethod()
                    .getClassFile()
                    .getConstantPool()
                    .getMethodRefDescriptor(code.nextUnsignedShort());

            invoke(context, methodRefDescriptor);

            return true;
        };
    }

    public static DecompilerExtension invokevirtual() {
        return (dc, cs, bc) -> true;
    }

    public static DecompilerExtension invokestatic() {
        return (dc, cs, bc) -> true;
    }

    private static void invoke(DecompilationContext context, MethodRefDescriptor methodReference) {
        final Signature signature = MethodSignature.parse(methodReference.getDescriptor());
        final Expression[] arguments = new Expression[signature.getParameterTypes().size()];
        final Type targetType = context.resolveType(methodReference.getClassName());

        for (int i = arguments.length - 1; i >= 0; i--) {
            arguments[i] = context.pop();
        }

        final Type expressionType;

        if (methodReference.getMethodName().equals("<init>")) {
            expressionType = targetType;
        } else {
            expressionType = signature.getReturnType();
        }

        context.push(new MethodCallImpl(
                targetType,
                methodReference.getMethodName(),
                signature,
                context.pop(),
                arguments,
                expressionType));
    }
}
