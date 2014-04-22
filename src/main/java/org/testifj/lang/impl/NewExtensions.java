package org.testifj.lang.impl;

import org.testifj.lang.ByteCode;
import org.testifj.lang.ClassFileFormatException;
import org.testifj.lang.DecompilerConfiguration;
import org.testifj.lang.DecompilerExtension;
import org.testifj.lang.model.AllocateInstance;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.MethodCall;
import org.testifj.lang.model.impl.AllocateInstanceImpl;
import org.testifj.lang.model.impl.NewInstanceImpl;

public final class NewExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.extend(ByteCode.new_, newInstance());
        configurationBuilder.enhance(ByteCode.invokespecial, (context, code, byteCode) -> {
            final MethodCall methodCall = (MethodCall) context.peek();

            if (methodCall.getMethodName().equals("<init>") && methodCall.getTargetInstance().getElementType() == ElementType.ALLOCATE) {
                final AllocateInstance allocateInstance = (AllocateInstance) methodCall.getTargetInstance();

                context.pop();
                context.push(new NewInstanceImpl(allocateInstance.getType(), methodCall.getSignature(), methodCall.getParameters()));
            }
        });
    }

    public static DecompilerExtension newInstance() {
        return (context, code, byteCode) -> {
            final String className = context.getMethod().getClassFile().getConstantPool().getClassName(code.nextUnsignedShort());

            context.push(new AllocateInstanceImpl(context.resolveType(className)));

            // Ignore the dup and model the constructor as returning an initialized instance instead
            if (code.nextInstruction() != ByteCode.dup) {
                throw new ClassFileFormatException("New byte code should always be dup:ed");
            }

            // Would like to install a listener on the stack here... like this

            return true;
        };
    }

}