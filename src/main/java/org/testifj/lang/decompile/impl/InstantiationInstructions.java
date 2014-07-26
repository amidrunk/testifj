package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.decompile.DecompilerConfigurationBuilder;
import org.testifj.lang.decompile.DecompilerDelegate;
import org.testifj.lang.decompile.DecompilerDelegation;
import org.testifj.lang.model.InstanceAllocation;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.MethodCall;
import org.testifj.lang.model.impl.InstanceAllocationImpl;
import org.testifj.lang.model.impl.NewInstanceImpl;

public final class InstantiationInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.new_).then(newInstance());
        configurationBuilder.after(ByteCode.invokespecial).then((context, code, byteCode) -> {
            final MethodCall methodCall = (MethodCall) context.peek();

            if (methodCall.getMethodName().equals("<init>") && methodCall.getTargetInstance().getElementType() == ElementType.ALLOCATE) {
                final InstanceAllocation instanceAllocation = (InstanceAllocation) methodCall.getTargetInstance();

                context.pop();
                context.push(new NewInstanceImpl(instanceAllocation.getType(), methodCall.getSignature(), methodCall.getParameters()));
            }
        });
    }

    public static DecompilerDelegate newInstance() {
        return (context, code, byteCode) -> {
            final String className = context.getMethod().getClassFile().getConstantPool().getClassName(code.nextUnsignedShort());

            context.push(new InstanceAllocationImpl(context.resolveType(className)));

            // Ignore the dup and model the constructor as returning an initialized instance instead
            if (code.nextInstruction() != ByteCode.dup) {
                throw new ClassFileFormatException("New byte code should always be dup:ed");
            }

            // TODO Would like to install a listener on the stack here... like this

        };
    }

}
