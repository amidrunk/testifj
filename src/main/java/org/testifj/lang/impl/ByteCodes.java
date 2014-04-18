package org.testifj.lang.impl;

import org.testifj.lang.DecompilationContext;
import org.testifj.lang.LocalVariable;
import org.testifj.lang.Method;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.impl.FieldReferenceImpl;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;
import org.testifj.lang.model.impl.VariableAssignmentImpl;

import java.lang.reflect.Type;

public final class ByteCodes {

    public static void loadVariable(DecompilationContext context, Method method, int index) {
        final LocalVariable localVariable = method.getLocalVariableForIndex(index);

        context.push(new LocalVariableReferenceImpl(localVariable.getName(), localVariable.getType(), index));
    }

    public static void storeVariable(DecompilationContext context, Method method, int index) {
        final LocalVariable localVariable = method.getLocalVariableForIndex(index);
        final Expression value = context.pop();

        context.push(new VariableAssignmentImpl(value, localVariable.getName(), localVariable.getType()));
    }

    public static void getStatic(DecompilationContext context, Type declaringType, Type fieldType, String fieldName) {
        context.push(new FieldReferenceImpl(null, declaringType, fieldType, fieldName));
    }

    public static void getField(DecompilationContext context, Type declaringType, Type fieldType, String fieldName) {
        context.push(new FieldReferenceImpl(context.pop(), declaringType, fieldType, fieldName));
    }

    public static void getField(DecompilationContext context, Type declaringType, Type fieldType, String fieldName, boolean isStatic) {
        if (isStatic) {
            getStatic(context, declaringType, fieldType, fieldName);
        } else {
            getField(context, declaringType, fieldType, fieldName);
        }
    }
}
