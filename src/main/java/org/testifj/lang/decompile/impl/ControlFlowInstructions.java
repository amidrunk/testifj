package org.testifj.lang.decompile.impl;

import org.testifj.lang.Types;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.impl.ReturnImpl;
import org.testifj.lang.model.impl.ReturnValueImpl;

import java.io.IOException;
import java.lang.reflect.Type;

public final class ControlFlowInstructions implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.return_).then((context,codeStream,byteCode) -> {
            context.reduceAll();
            context.enlist(new ReturnImpl());
        });

        configurationBuilder.on(ByteCode.ireturn).then(ireturn());
        configurationBuilder.on(ByteCode.lreturn).then(lreturn());
        configurationBuilder.on(ByteCode.freturn).then(freturn());
        configurationBuilder.on(ByteCode.dreturn).then(dreturn());
        configurationBuilder.on(ByteCode.areturn).then(areturn());
    }

    public static DecompilerDelegate ireturn() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression returnValue = context.getStack().pop();

                switch (returnValue.getType().getTypeName()) {
                    case "boolean":
                    case "byte":
                    case "short":
                    case "char":
                    case "int":
                        context.enlist(new ReturnValueImpl(returnValue));
                        break;
                    default:
                        throw invalidReturnValue(byteCode, returnValue);
                }
            }
        };
    }

    public static DecompilerDelegate lreturn() {
        return xreturn(long.class);
    }

    public static DecompilerDelegate freturn() {
        return xreturn(float.class);
    }

    public static DecompilerDelegate dreturn() {
        return xreturn(double.class);
    }

    public static DecompilerDelegate areturn() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression returnValue = context.getStack().pop();

                if (Types.isPrimitive(returnValue.getType())) {
                    throw invalidReturnValue(byteCode, returnValue);
                }

                context.enlist(new ReturnValueImpl(returnValue));
            }
        };
    }

    private static DecompilerDelegate xreturn(Type type) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression returnValue = context.getStack().pop();

                if (!returnValue.getType().equals(type)) {
                    throw invalidReturnValue(byteCode, returnValue);
                }

                context.enlist(new ReturnValueImpl(returnValue));
            }
        };
    }

    private static ClassFileFormatException invalidReturnValue(int byteCode, Expression returnValue) {
        return new ClassFileFormatException("Invalid return value on stack: "
                + ByteCode.toString(byteCode) + " can't return " + returnValue);
    }
}
