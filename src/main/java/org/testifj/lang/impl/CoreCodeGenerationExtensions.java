package org.testifj.lang.impl;

import org.testifj.lang.CodeGeneratorConfiguration;
import org.testifj.lang.CodeGeneratorExtension;
import org.testifj.lang.model.*;
import org.testifj.util.Joiner;

import java.util.Arrays;
import java.util.Iterator;

/**
 * TODO Instead use 'builder.extend(METHOD_CALL, cp -> cp.element(MethodCall.class).getTargetInstance() == null, (...) -> ); ?
 * The above can be reduced to:
 *
 * builder.extend(METHOD_CALL, staticMethod(), staticMethodCall());
 */
public final class CoreCodeGenerationExtensions {

    public static void configure(CodeGeneratorConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.extend(ElementType.RETURN, ret());
        configurationBuilder.extend(ElementType.RETURN_VALUE, returnValue());
        configurationBuilder.extend(ElementType.CONSTANT, constant());
        configurationBuilder.extend(ElementType.VARIABLE_REFERENCE, variableReference());
        configurationBuilder.extend(ElementType.METHOD_CALL, instanceMethodCall());
        configurationBuilder.extend(ElementType.METHOD_CALL, staticMethodCall());
    }

    public static CodeGeneratorExtension constant() {
        return (context, codePointer, out) -> {
            final Constant constant = (Constant) codePointer.getElement();

            if (constant.getType().equals(String.class)) {
                out.append('"').append(String.valueOf(constant.getConstant())).append('"');
            } else if (constant.getType().equals(long.class)) {
                out.append(String.valueOf(constant.getConstant())).append('L');
            } else if (constant.getType().equals(float.class)) {
                out.append(String.valueOf(constant.getConstant())).append('f');
            } else {
                out.append(String.valueOf(constant.getConstant()));
            }

            return true;
        };
    }

    public static CodeGeneratorExtension ret() {
        return (context, codePointer, out) -> {
            out.append("return");
            return true;
        };
    }

    public static CodeGeneratorExtension variableReference() {
        return (context, codePointer, out) -> {
            final LocalVariableReference variableReference = (LocalVariableReference) codePointer.getElement();
            out.append(variableReference.getName());
            return true;
        };
    }

    public static CodeGeneratorExtension returnValue() {
        return (context, codePointer, out) -> {
            final ReturnValue returnValue = (ReturnValue) codePointer.getElement();
            out.append("return ");
            context.delegate(codePointer.forElement(returnValue.getValue()));
            return true;
        };
    }

    public static CodeGeneratorExtension staticMethodCall() {
        return (context, codePointer, out) -> {
            final MethodCall methodCall = (MethodCall) codePointer.getElement();

            if (methodCall.getTargetInstance() != null) {
                return false;
            }

            out.append(context.getCodeStyle().getTypeName(methodCall.getTargetType()))
                    .append('.')
                    .append(methodCall.getMethodName())
                    .append('(');

            for (Iterator<Expression> i = methodCall.getParameters().iterator(); i.hasNext(); ) {
                context.delegate(codePointer.forElement(i.next()));

                if (i.hasNext()) {
                    out.append(", ");
                }
            }

            out.append(')');
            return true;
        };
    }

    public static CodeGeneratorExtension instanceMethodCall() {
        return (context, codePointer, out) -> {
            final MethodCall methodCall = (MethodCall) codePointer.getElement();

            if (methodCall.getTargetInstance() == null) {
                return false;
            }

            context.delegate(codePointer.forElement(methodCall.getTargetInstance()));
            out.append('.').append(methodCall.getMethodName()).append('(');

            for (Iterator<Expression> i = methodCall.getParameters().iterator(); i.hasNext(); ) {
                context.delegate(codePointer.forElement(i.next()));

                if (i.hasNext()) {
                    out.append(", ");
                }
            }

            out.append(')');
            return true;
        };
    }

}
