package org.testifj.lang.impl;

import org.testifj.lang.ClassFileFormatException;
import org.testifj.lang.DecompilationContext;
import org.testifj.lang.LocalVariable;
import org.testifj.lang.Method;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;

public final class ByteCodes {

    public static void loadVariable(DecompilationContext context, Method method, int index, Class<?> expectedType) {
        final LocalVariable localVariable = method.getLocalVariableForIndex(index);

        if (expectedType.isPrimitive()) {
            if (!localVariable.getType().equals(expectedType)) {
                throw new ClassFileFormatException("Expected type of local variable '" + localVariable.getName()
                        + "' to be " + expectedType.getName()
                        + ", but was " + localVariable.getType().getTypeName());
            }
        } else if (localVariable.getType() instanceof Class && ((Class) localVariable.getType()).isPrimitive()) {
            throw new ClassFileFormatException("Expected local variable '" + localVariable.getName()
                    + "' to be non-primitive, was " + ((Class) localVariable.getType()).getName());
        }

        context.push(new LocalVariableReferenceImpl(localVariable.getName(), localVariable.getType(), index));
    }

}
