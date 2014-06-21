package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.FieldRefDescriptor;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.FieldReference;
import org.testifj.lang.model.impl.FieldAssignmentImpl;
import org.testifj.lang.model.impl.FieldReferenceImpl;
import org.testifj.lang.model.impl.MethodSignature;

import java.io.IOException;

public final class FieldDecompilationDelegation implements DecompilerDelegation {

    /**
     * Configures the provided configuration builder with support for byte codes related to field
     * access and assignment.
     *
     * @param configurationBuilder The configuration builder to configure.
     */
    public void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.putfield).then(putfield());
        configurationBuilder.on(ByteCode.putstatic).then(putstatic());
    }

    /**
     * Extension for the <code>putfield=181</code> byte code. Will load the field descriptor from the
     * subsequent two bytes in the code stream pop the value and target instance from the stack and
     * push a <code>{@link org.testifj.lang.model.FieldAssignment}</code> back onto the stack.
     *
     * @return A <code>DecompilerExtension</code> that handles the <code>putfield</code> byte code.
     */
    public static DecompilerDelegate putfield() {
        return (context, codeStream, byteCode) -> {
            handlePutField(context, codeStream, false);
        };
    }

    /**
     * Extension for the <code>putstatic=179</code> byte code. Will load the descriptor from the subsequent
     * two bytes in the code stream, pop the field value onto the stack and push a
     * {@link org.testifj.lang.model.FieldAssignment} back onto the stack.
     *
     * @return A <code>DecompilerExtension</code> that handles the <code>putstatic</code> byte code.
     */
    public static DecompilerDelegate putstatic() {
        return (context,codeStream,byteCode) -> {
            handlePutField(context, codeStream, true);
        };
    }

    private static void handlePutField(DecompilationContext context, CodeStream codeStream, boolean isStatic) throws IOException {
        final ConstantPool constantPool = context.getMethod().getClassFile().getConstantPool();
        final FieldRefDescriptor fieldRefDescriptor = constantPool.getFieldRefDescriptor(codeStream.nextUnsignedShort());
        final Expression value = context.pop();
        final Expression targetInstance = (isStatic ? null : context.pop());
        final FieldReference fieldReference = new FieldReferenceImpl(
                targetInstance,
                context.resolveType(fieldRefDescriptor.getClassName()),
                MethodSignature.parseType(fieldRefDescriptor.getDescriptor()),
                fieldRefDescriptor.getName());

        context.enlist(new FieldAssignmentImpl(fieldReference, value));
    }

}
