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

import java.io.IOException;
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
        configurationBuilder.on(ByteCode.invokevirtual).then(invokespecial());
        configurationBuilder.on(ByteCode.invokestatic).then(invokestatic());
    }

    public static DecompilerDelegate invokeinterface() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final InterfaceMethodRefDescriptor interfaceMethodRefDescriptor = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getInterfaceMethodRefDescriptor(codeStream.nextUnsignedShort());

                invoke(context, interfaceMethodRefDescriptor, false);

                if (codeStream.nextUnsignedByte() == 0) {
                    throw new ClassFileFormatException("Expected byte subsequent to interface method invocation to be non-zero");
                }
            }
        };
    }

    public static DecompilerDelegate invokespecial() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final MethodRefDescriptor methodRefDescriptor = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getMethodRefDescriptor(codeStream.nextUnsignedShort());

                invoke(context, methodRefDescriptor, false);
            }
        };
    }

    public static DecompilerDelegate invokevirtual() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final MethodRefDescriptor methodRefDescriptor = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getMethodRefDescriptor(codeStream.nextUnsignedShort());

                invoke(context, methodRefDescriptor, false);
            }
        };
    }

    public static DecompilerDelegate invokestatic() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final MethodRefDescriptor methodRefDescriptor = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getMethodRefDescriptor(codeStream.nextUnsignedShort());

                invoke(context, methodRefDescriptor, true);
            }
        };
    }

    private static void invoke(DecompilationContext context, MethodRefDescriptor methodReference, boolean isStatic) {
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

        final Expression thiz;

        if (isStatic) {
            thiz = null;
        } else {
            thiz = context.pop();
        }

        context.push(new MethodCallImpl(
                targetType,
                methodReference.getMethodName(),
                signature,
                thiz,
                arguments,
                expressionType));
    }
}