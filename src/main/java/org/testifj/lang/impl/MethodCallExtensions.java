package org.testifj.lang.impl;

import org.testifj.lang.DecompilerConfiguration;
import org.testifj.lang.DecompilerExtension;
import org.testifj.lang.InterfaceMethodRefDescriptor;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Signature;
import org.testifj.lang.model.impl.MethodCallImpl;
import org.testifj.lang.model.impl.SignatureImpl;

import java.lang.reflect.Type;

public final class MethodCallExtensions {

    public static void configure(DecompilerConfiguration configure) {
    }

    public static DecompilerExtension invokeinterface() {
        return (context, code, instruction) -> {
            final InterfaceMethodRefDescriptor descriptor = context.getMethod().getClassFile().getConstantPool().getInterfaceMethodRefDescriptor(code.nextUnsignedShort());
            final Signature signature = SignatureImpl.parse(descriptor.getDescriptor());
            final Expression[] arguments = new Expression[signature.getParameterTypes().size()];

            for (int i = arguments.length - 1; i >= 0; i--) {
                arguments[i] = context.pop();
            }

            context.push(new MethodCallImpl(
                    context.resolveType(descriptor.getClassName()),
                    descriptor.getMethodName(),
                    SignatureImpl.parse(descriptor.getDescriptor()),
                    context.pop(),
                    arguments));

            return true;
        };
    }

    public static DecompilerExtension invokevirtual() {
        return (dc, cs, bc) -> true;
    }

    public static DecompilerExtension invokespecial() {
        return (dc, cs, bc) -> true;
    }

    public static DecompilerExtension invokestatic() {
        return (dc, cs, bc) -> true;
    }
}
