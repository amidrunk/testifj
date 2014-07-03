package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.classfile.InterfaceMethodRefDescriptor;
import org.testifj.lang.classfile.MethodRefDescriptor;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;
import org.testifj.lang.model.Signature;
import org.testifj.lang.model.impl.MethodCallImpl;
import org.testifj.lang.model.impl.MethodSignature;
import org.testifj.util.Iterators;
import org.testifj.util.Lists;
import org.testifj.util.Pair;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.testifj.lang.decompile.DecompilationStateSelectors.elementIsStacked;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.util.Lists.optionallyCollect;
import static org.testifj.util.Lists.zip;

public final class MethodCallInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.invokeinterface).then(invokeinterface());
        configurationBuilder.on(ByteCode.invokespecial).then(invokespecial());
    }

    public static DecompilerDelegate invokeinterface() {
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
        };
    }

    public static DecompilerDelegate invokespecial() {
        return (context, code, instruction) -> {
            final MethodRefDescriptor methodRefDescriptor = context
                    .getMethod()
                    .getClassFile()
                    .getConstantPool()
                    .getMethodRefDescriptor(code.nextUnsignedShort());

            invoke(context, methodRefDescriptor);
        };
    }

    // TODO Implement
    public static DecompilerDelegate invokevirtual() {
        return (dc, cs, bc) -> {};
    }

    // TODO Implement
    public static DecompilerDelegate invokestatic() {
        return (dc, cs, bc) -> {};
    }

    private static void invoke(DecompilationContext context, MethodRefDescriptor methodReference) {
        final Signature signature = MethodSignature.parse(methodReference.getDescriptor());
        final Expression[] arguments = new Expression[signature.getParameterTypes().size()];
        final Type targetType = context.resolveType(methodReference.getClassName());

        for (int i = arguments.length - 1; i >= 0; i--) {
            arguments[i] = context.pop();
        }

        final Type expressionType;

        if (methodReference.getMethodName().equals("<init>")) { // TODO Correctional enhancement
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
