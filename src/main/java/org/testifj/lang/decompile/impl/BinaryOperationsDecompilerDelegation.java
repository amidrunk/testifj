package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;
import org.testifj.lang.model.impl.BinaryOperatorImpl;

import java.io.IOException;

public final class BinaryOperationsDecompilerDelegation implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfiguration.Builder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompiler configuration builder can't be null";

        decompilerConfigurationBuilder.on(ByteCode.iadd).then(iadd());
        decompilerConfigurationBuilder.on(ByteCode.isub).then(isub());
        decompilerConfigurationBuilder.on(ByteCode.imul).then(imul());
        decompilerConfigurationBuilder.on(ByteCode.idiv).then(idiv());
    }

    public static DecompilerExtension iadd() {
        return binaryOperator(OperatorType.PLUS, int.class);
    }

    public static DecompilerExtension isub() {
        return binaryOperator(OperatorType.MINUS, int.class);
    }

    public static DecompilerExtension imul() {
        return binaryOperator(OperatorType.MULTIPLY, int.class);
    }

    public static DecompilerExtension idiv() {
        return binaryOperator(OperatorType.DIVIDE, int.class);
    }

    private static DecompilerExtension binaryOperator(final OperatorType operatorType, final Class<Integer> resultType) {
        return new DecompilerExtension() {
            @Override
            public boolean decompile(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression right = context.pop();
                final Expression left = context.pop();

                context.push(new BinaryOperatorImpl(left, operatorType, right, resultType));

                return true;
            }
        };
    }

}
