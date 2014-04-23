package org.testifj.lang.impl;

import org.testifj.annotations.DSL;
import org.testifj.lang.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;

import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.function.Predicate;

public final class CoreCodeGenerationExtensions {

    public static void configure(CodeGeneratorConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.extend(ElementSelector.forType(ElementType.RETURN), ret());
        configurationBuilder.extend(ElementSelector.forType(ElementType.CONSTANT), constant());
        configurationBuilder.extend(ElementSelector.forType(ElementType.RETURN_VALUE), returnValue());
        configurationBuilder.extend(ElementSelector.forType(ElementType.CONSTANT), constant());
        configurationBuilder.extend(ElementSelector.forType(ElementType.VARIABLE_REFERENCE), variableReference());
        configurationBuilder.extend(selectBooleanBoxCall(), boxBooleanExtension());
        configurationBuilder.extend(selectDSLMethodCall(), dslMethodCallExtension());
        configurationBuilder.extend(selectInstanceMethodCall(), instanceMethodCallExtension());
        configurationBuilder.extend(selectStaticMethodCall(), staticMethodCallExtension());
    }

    public static CodeGeneratorExtension<Constant> constant() {
        return (context, codePointer, out) -> {
            final Type type = codePointer.getElement().getType();
            final Object constant = codePointer.getElement().getConstant();

            if (type.equals(String.class)) {
                out.append('"').append(String.valueOf(constant)).append('"');
            } else if (type.equals(long.class)) {
                out.append(String.valueOf(constant)).append('L');
            } else if (type.equals(float.class)) {
                out.append(String.valueOf(constant)).append('f');
            } else {
                out.append(String.valueOf(constant));
            }
        };
    }

    public static CodeGeneratorExtension<Return> ret() {
        return (context, codePointer, out) -> {
            out.append("return");
        };
    }

    public static CodeGeneratorExtension<LocalVariableReference> variableReference() {
        return (context, codePointer, out) -> {
            out.append(codePointer.getElement().getName());
        };
    }

    public static CodeGeneratorExtension<ReturnValue> returnValue() {
        return (context, codePointer, out) -> {
            out.append("return ");
            context.delegate(codePointer.forElement(codePointer.getElement().getValue()));
        };
    }

    public static Predicate<CodePointer<MethodCall>> isStaticMethodCall() {
        return cp -> cp.getElement().getTargetInstance() == null;
    }

    public static Predicate<CodePointer<MethodCall>> isInstanceMethodCall() {
        return cp -> cp.getElement().getTargetInstance() != null;
    }

    public static ElementSelector<MethodCall> selectStaticMethodCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isStaticMethodCall());
    }

    public static ElementSelector<MethodCall> selectInstanceMethodCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isInstanceMethodCall());
    }

    /**
     * Creates a selector for the boolean box call that occurs when a boolean is assigned to / used
     * in place of a java.lang.Boolean instance. The selector will match a static method call against
     * java.lang.Boolean.valueOf with signature (Z)Ljava/lang/Boolean;
     *
     * @return An element selector for the boolean box call.
     */
    public static ElementSelector<MethodCall> selectBooleanBoxCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isStaticMethodCall().and(cp -> {
            final MethodCall methodCall = cp.getElement();

            if (!methodCall.getTargetType().equals(Boolean.class)) {
                return false;
            }

            if (!methodCall.getSignature().toString().equals("(Z)Ljava/lang/Boolean;")) {
                return false;
            }

            final Expression parameter = methodCall.getParameters().get(0);

            if (!parameter.getType().equals(int.class)) {
                return false;
            }

            if (parameter.getElementType() != ElementType.CONSTANT) {
                return false;
            }

            return true;
        }));
    }

    /**
     * Extension for boxing of a boolean. Boxing is implicit in the language, but will result in
     * a call to <code>java.lang.Boolean.valueOf(boolean)</code> in the byte code. This extension
     * will generate code that uses implicit boxing.
     *
     * @return A code generator extension that translates Boolean.valueOf(boolean) to an
     * implicit boolean boxing.
     */
    public static CodeGeneratorExtension<MethodCall> boxBooleanExtension() {
        return (context, codePointer, out) -> {
            final Constant parameter = (Constant) codePointer.getElement().getParameters().get(0);

            if (parameter.getConstant().equals(0)) {
                out.print("false");
            } else {
                out.print("true");
            }
        };
    }

    /**
     * Extension for handling of static methods, i.e. method that are invoked on a class rather
     * than on a target instance. Parameters will be delegated to the context, i.e. the generation
     * will be className(delegate(param_1), ..., delegate(param_n)) where the class name is
     * retrieved from the code style (allows for qualified or unqualified references).
     *
     * @return A code generator extension for static calls.
     */
    public static CodeGeneratorExtension<MethodCall> staticMethodCallExtension() {
        return (context, codePointer, out) -> {
            final MethodCall methodCall = codePointer.getElement();

            out.append(context.getCodeStyle().getTypeName(methodCall.getTargetType()))
                    .append('.');

            appendMethodCall(context, codePointer, out);
        };
    }

    /**
     * Extensions for plain instance method calls. This extension will delegate code generation for
     * the instance that is target of the method call and the parameters of the method call:
     * delegate(targetInstance).methodName(delegate(param_1), ..., delegate(param_n))
     *
     * @return A code generator extension for method invocations on an instance.
     */
    public static CodeGeneratorExtension<MethodCall> instanceMethodCallExtension() {
        return (context, codePointer, out) -> {
            final MethodCall methodCall = codePointer.getElement();
            final Expression targetInstance = methodCall.getTargetInstance();

            if (!context.getCodeStyle().shouldOmitThis()
                    || targetInstance.getElementType() != ElementType.VARIABLE_REFERENCE
                    || !((LocalVariableReference) targetInstance).getName().equals("this")) {
                context.delegate(codePointer.forElement(targetInstance));
                out.append('.');
            }

            appendMethodCall(context, codePointer, out);
        };
    }

    /**
     * Returns a predicate that determines whether or not a method call is a DSL method call. This
     * is true iff the target type is (1) static and (2) the target type has the @DSL annotation.
     *
     * @return A predicate that can test whether or not a method call element represents a DSL call.
     */
    public static Predicate<CodePointer<MethodCall>> isDSLMethodCall() {
        return isStaticMethodCall().and(codePointer -> {
            final Type targetType = codePointer.getElement().getTargetType();

            if (!(targetType instanceof Class)) {
                return false;
            }

            return ((Class) targetType).getAnnotation(DSL.class) != null;
        });
    }

    /**
     * Creates an element selector that matches method calls that are (1) static and (2) called on
     * a type that has the @DSL annotation. See {@link CoreCodeGenerationExtensions#isDSLMethodCall()}.
     * 
     * @return A selector that selects DSL method calls.
     */
    public static ElementSelector<MethodCall> selectDSLMethodCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isDSLMethodCall());
    }

    /**
     * Creates a code generator extension that handles DSL method calls. DSL method calls will omit
     * the target type.
     *
     * @return A code generator extension that handles DSL method calls.
     */
    public static CodeGeneratorExtension<MethodCall> dslMethodCallExtension() {
        return CoreCodeGenerationExtensions::appendMethodCall;
    }

    /**
     * Appends a method call to the provided print writer. The target of the method call is assumed to have been
     * appended; this method will append the method name and the parameter list.
     *
     * @param context The context in which the method call code is generated.
     * @param codePointer The code pointer referencing the method call.
     * @param out The print writer to which the generated code is written.
     */
    private static void appendMethodCall(CodeGenerationContext context, CodePointer<MethodCall> codePointer, PrintWriter out) {
        final MethodCall methodCall = codePointer.getElement();

        out.append(methodCall.getMethodName()).append('(');

        for (Iterator<Expression> i = methodCall.getParameters().iterator(); i.hasNext(); ) {
            context.delegate(codePointer.forElement(i.next()));

            if (i.hasNext()) {
                out.append(", ");
            }
        }

        out.append(')');
    }

}
