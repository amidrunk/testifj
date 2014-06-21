package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;
import org.testifj.lang.model.impl.BinaryOperatorImpl;

import java.io.IOException;

/**
 * The <code>BinaryOperationsDecompilerDelegation</code> provides handing of all binary operations
 * during decompilation, from iadd=96 to lxor=131.
 */
public final class BinaryOperationsDecompilerDelegation implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfiguration.Builder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompiler configuration builder can't be null";

        decompilerConfigurationBuilder.on(ByteCode.iadd).then(iadd());
        decompilerConfigurationBuilder.on(ByteCode.isub).then(isub());
        decompilerConfigurationBuilder.on(ByteCode.imul).then(imul());
        decompilerConfigurationBuilder.on(ByteCode.idiv).then(idiv());
    }

    public static DecompilerDelegate iadd() {
        return binaryOperator(OperatorType.PLUS, int.class);
    }

    public static DecompilerDelegate isub() {
        return binaryOperator(OperatorType.MINUS, int.class);
    }

    public static DecompilerDelegate imul() {
        return binaryOperator(OperatorType.MULTIPLY, int.class);
    }

    public static DecompilerDelegate idiv() {
        return binaryOperator(OperatorType.DIVIDE, int.class);
    }

    private static DecompilerDelegate binaryOperator(final OperatorType operatorType, final Class<Integer> resultType) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression right = context.pop();
                final Expression left = context.pop();

                context.push(new BinaryOperatorImpl(left, operatorType, right, resultType));
            }
        };
    }

}
