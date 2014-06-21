package org.testifj.lang.decompile.impl;

import org.testifj.annotations.DSL;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.*;
import org.testifj.lang.classfile.impl.MethodReferenceImpl;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.MethodSignature;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;

public final class CoreCodeGenerationExtensions {

    public static void configure(CodeGeneratorConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.extend(ElementSelector.forType(ElementType.RETURN), ret());
        configurationBuilder.extend(ElementSelector.forType(ElementType.CONSTANT), constant());
        configurationBuilder.extend(ElementSelector.forType(ElementType.RETURN_VALUE), returnValue());
        configurationBuilder.extend(ElementSelector.forType(ElementType.CONSTANT), constant());
        configurationBuilder.extend(ElementSelector.forType(ElementType.VARIABLE_REFERENCE), variableReference());
        configurationBuilder.extend(ElementSelector.forType(ElementType.ARRAY_STORE), arrayStoreExtension());
        configurationBuilder.extend(ElementSelector.forType(ElementType.CAST), castExtension());
        configurationBuilder.extend(ElementSelector.forType(ElementType.ARRAY_LOAD), arrayLoadExtension());
        configurationBuilder.extend(ElementSelector.forType(ElementType.ALLOCATE), allocateInstanceExtension());
        configurationBuilder.extend(ElementSelector.forType(ElementType.INCREMENT), increment());

        configurationBuilder.extend(selectBooleanBoxCall(), boxBooleanExtension());
        configurationBuilder.extend(selectPrimitiveBoxCall(), primitiveBoxCallExtension());
        configurationBuilder.extend(selectDSLMethodCall(), dslMethodCallExtension());
        configurationBuilder.extend(selectInnerClassFieldAccess(), innerClassFieldAccessExtension());
        configurationBuilder.extend(selectInstanceMethodCall(), instanceMethodCallExtension());
        configurationBuilder.extend(selectStaticMethodCall(), staticMethodCallExtension());
        configurationBuilder.extend(selectUninitializedNewArray(), newUninitializedArrayExtension());
        configurationBuilder.extend(selectInitializedNewArray(), newInitializedArrayExtension());
    }

    public static CodeGeneratorExtension<Increment> increment() {
        return (context,codePointer,out) -> {
            context.delegate(codePointer.forElement(codePointer.getElement().getOperand()));
            out.append("++");
        };
    }

    /**
     * Extension for the {@link org.testifj.lang.model.ElementType#ALLOCATE} model element. This element
     * is discarded during decompilation, since it doesn't correspond to a Java syntax element. However,
     * generation of the element is required to generate code for intermediate decompilations, e.g. during
     * debug.
     *
     * @return Code generator extension for {@link org.testifj.lang.model.ElementType#ALLOCATE}, which
     * corresponds to the {@link org.testifj.lang.classfile.ByteCode#new_} byte code.
     */
    public static CodeGeneratorExtension<AllocateInstance> allocateInstanceExtension() {
        return (context,codePointer,out) -> {
            final AllocateInstance allocateInstance = codePointer.getElement();

            out.append("new ").append(context.getCodeStyle().getTypeName(allocateInstance.getType())).append("<uninitialized>");
        };
    }

    /**
     * Handles loading of array elements. This corresponds to the byte code {@link org.testifj.lang.classfile.ByteCode#aaload}
     * and the model element {@link org.testifj.lang.model.ElementType#ARRAY_LOAD}.
     *
     * @return A code generator extension for handling array element access.
     */
    public static CodeGeneratorExtension<ArrayLoad> arrayLoadExtension() {
        return (context,codePointer,out) -> {
            final ArrayLoad arrayLoad = codePointer.getElement();

            context.delegate(codePointer.forElement(arrayLoad.getArray()));
            out.append("[");
            context.delegate(codePointer.forElement(arrayLoad.getIndex()));
            out.append("]");
        };
    }

    /**
     * Extension for a type cast, i.e. the <code>{@link org.testifj.lang.classfile.ByteCode#checkcast}</code> instruction,
     * which is matched by a {@link org.testifj.lang.model.ElementType#CAST} element. The output of the extension
     * is <code>(typeName)delegate(value)</code> where type name is composed from the active code style.
     *
     * @return An extension that handles the {@link org.testifj.lang.model.ElementType#CAST} element.
     */
    public static CodeGeneratorExtension<Cast> castExtension() {
        return (context,codePointer,out) -> {
            final Cast cast = codePointer.getElement();
            final String targetTypeName = context.getCodeStyle().getTypeName(cast.getType());

            out.append("(").append(targetTypeName).append(")");
            context.delegate(codePointer.forElement(cast.getValue()));
        };
    }

    /**
     * <p>
     * Returns whether or not a method call is a field access of an inner class. Directly accessed fields
     * of inner classes are not implemented using getfield, but rather a private accessor method is
     * automatically generated by the compiler. For example, assume the following code:
     * </p>
     * <p>
     * <pre><code>
     * public class Outer {
     *
     *     private Inner inner = new Inner();
     *
     *     public String getString() { return inner.str; }
     *
     *     public static class Inner {
     *         private String str;
     *     }
     *
     * }
     * </code></pre>
     * </p>
     * <p>
     * The reference to <code>inner.str</code> in the example above will not be implemented as
     * <ul style="diamond:none;">
     *     <li>aload_0</li>
     *     <li>getfield inner</li>
     *     <li>getfield str</li>
     * </ul>
     * </p>
     * Rather a method will be generated in the inner class, resulting in the following code:
     * <pre><code>
     * public class Outer {
     *     public Inner inner;
     *
     *     public String getString() {
     *         return Inner.access$100(inner);
     *     }
     *
     *     public static class Inner {
     *         private String str;
     *
     *         private static String access$100(Inner inner) {
     *             return inner.str;
     *         }
     *     }
     * }
     * </code></pre>
     *
     * @return An element selector that selects a method call iff it represents an inner class field access.
     */
    public static ElementSelector<MethodCall> selectInnerClassFieldAccess() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isStaticMethodCall().and(codePointer -> {
            final MethodCall methodCall = codePointer.getElement();

            if (!methodCall.getMethodName().startsWith("access$")) {
                return false;
            }

            final List<Expression> parameters = methodCall.getParameters();

            if (parameters.size() != 1 && parameters.size() != 2) {
                return false;
            }

            final Expression parameterValue = parameters.get(0);

            if (!parameterValue.getType().equals(methodCall.getTargetType())) {
                return false;
            }

            return true;
        }));
    }

    public static CodeGeneratorExtension<MethodCall> innerClassFieldAccessExtension() {
        return (context, codePointer, out) -> {
            final MethodCall methodCall = codePointer.getElement();
            final ClassFile innerClassClassFile = context.getClassFileResolver().resolveClassFile(methodCall.getTargetType());
            final Method method = innerClassClassFile.getMethods().stream()
                    .filter(m -> m.getName().equals(methodCall.getMethodName()))
                    .findFirst()
                    .orElseThrow(() -> new CodeGenerationException("Could not find accessor method "
                            + methodCall.getTargetType().getTypeName() + "." + methodCall.getMethodName() + " in class file"));

            final Element[] methodElements;

            try (CodeStream code = new InputStreamCodeStream(method.getCode().getCode())) {
                methodElements = context.getDecompiler().parse(method, code);
            } catch (IOException e) {
                throw new CodeGenerationException("Failed to decompile method '" + method.getName()
                        + "' in class '" + innerClassClassFile.getName() + "'", e);
            }

            context.delegate(codePointer.forElement(methodCall.getParameters().get(0)));
            out.append(".");

            if (methodCall.getParameters().size() == 1) {
                out.append(((FieldReference) ((ReturnValue) methodElements[0]).getValue()).getFieldName());
            } else {
                out.append(((FieldAssignment) methodElements[0]).getFieldReference().getFieldName()).append(" = ");
                context.delegate(codePointer.forElement(methodCall.getParameters().get(1)));
            }
        };
    }

    /**
     * Selects a {@link org.testifj.lang.model.NewArray} with no initializers specified. Will create
     * a new array with default values.
     *
     * @return A selector that selects {@link org.testifj.lang.model.NewArray} elements without
     * and initializers.
     */
    public static ElementSelector<NewArray> selectUninitializedNewArray() {
        return ElementSelector.<NewArray>forType(ElementType.NEW_ARRAY)
                .where(cp -> cp.getElement().getInitializers().isEmpty());
    }

    /**
     * Selects {@link org.testifj.lang.model.NewArray} elements with initializers specified.
     *
     * @return A selector that selects initialized new arrays.
     */
    public static ElementSelector<NewArray> selectInitializedNewArray() {
        return ElementSelector.<NewArray>forType(ElementType.NEW_ARRAY)
                .where(cp -> !cp.getElement().getInitializers().isEmpty());
    }

    /**
     * Code generator extension for uninitialized new arrays. Will generate code on the form
     * <code>new T:ClassName[n:Expression]</code>.
     *
     * @return A code generator extension that handles {@link org.testifj.lang.model.NewArray}-elements
     * that has no initializers.
     */
    public static CodeGeneratorExtension<NewArray> newUninitializedArrayExtension() {
        return (context, codePointer, out) -> {
            final NewArray newArray = codePointer.getElement();

            out.append("new ").append(context.getCodeStyle().getTypeName(newArray.getComponentType())).append("[");
            context.delegate(codePointer.forElement(newArray.getLength()));
            out.append("]");
        };
    }

    public static CodeGeneratorExtension<NewArray> newInitializedArrayExtension() {
        return (context, codePointer, out) -> {
            final NewArray newArray = codePointer.getElement();

            out.append("new ").append(context.getCodeStyle().getTypeName(newArray.getComponentType())).append("[] { ");

            for (Iterator<ArrayInitializer> i = newArray.getInitializers().iterator(); i.hasNext(); ) {
                context.delegate(codePointer.forElement(i.next().getValue()));

                if (i.hasNext()) {
                    out.append(", ");
                }
            }

            out.append(" }");
        };
    }

    /**
     * Extension for assignment to array element.
     *
     * @return A code generator extension that handles {@link org.testifj.lang.model.ArrayStore}-elements.
     */
    public static CodeGeneratorExtension<ArrayStore> arrayStoreExtension() {
        return (context, codePointer, out) -> {
            final ArrayStore arrayStore = codePointer.getElement();

            context.delegate(codePointer.forElement(arrayStore.getArray()));
            out.append("[");
            context.delegate(codePointer.forElement(arrayStore.getIndex()));
            out.append("] = ");
            context.delegate(codePointer.forElement(arrayStore.getValue()));
        };
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
            } else if (type.equals(Class.class)) {
                out.append(context.getCodeStyle().getTypeName((Type) constant)).append(".class");
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

            if (!parameter.getType().equals(int.class) && !parameter.getType().equals(boolean.class)) {
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
            final Expression parameter = codePointer.getElement().getParameters().get(0);

            if (parameter.getElementType() == ElementType.CONSTANT && parameter.getType().equals(int.class)) {
                if (parameter.as(Constant.class).getConstant().equals(0)) {
                    out.print("false");
                } else {
                    out.print("true");
                }
            } else {
                context.delegate(codePointer.forElement(parameter));
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

    private static final Set<MethodReference> PRIMITIVE_BOX_METHODS = new HashSet<>(Arrays.<MethodReference>asList(
            new MethodReferenceImpl(Byte.class, "valueOf", MethodSignature.parse("(B)Ljava/lang/Byte;")),
            new MethodReferenceImpl(Short.class, "valueOf", MethodSignature.parse("(S)Ljava/lang/Short;")),
            new MethodReferenceImpl(Character.class, "valueOf", MethodSignature.parse("(C)Ljava/lang/Character;")),
            new MethodReferenceImpl(Integer.class, "valueOf", MethodSignature.parse("(I)Ljava/lang/Integer;")),
            new MethodReferenceImpl(Long.class, "valueOf", MethodSignature.parse("(J)Ljava/lang/Long;")),
            new MethodReferenceImpl(Float.class, "valueOf", MethodSignature.parse("(F)Ljava/lang/Float;")),
            new MethodReferenceImpl(Double.class, "valueOf", MethodSignature.parse("(D)Ljava/lang/Double;"))
    ));

    public static Predicate<CodePointer<MethodCall>> isPrimitiveBoxCall() {
        return isStaticMethodCall().and(cp -> {
            final MethodCall methodCall = cp.getElement();

            return PRIMITIVE_BOX_METHODS.contains(new MethodReferenceImpl(
                    methodCall.getTargetType(),
                    methodCall.getMethodName(),
                    methodCall.getSignature()));
        });
    }

    public static ElementSelector<MethodCall> selectPrimitiveBoxCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isPrimitiveBoxCall());
    }

    public static CodeGeneratorExtension<MethodCall> primitiveBoxCallExtension() {
        return (context, codePointer, out) -> {
            context.delegate(codePointer.forElement(codePointer.getElement().getParameters().get(0)));
        };
    }

    /**
     * Appends a method call to the provided print writer. The target of the method call is assumed to have been
     * appended; this method will append the method name and the parameter list.
     *
     * @param context     The context in which the method call code is generated.
     * @param codePointer The code pointer referencing the method call.
     * @param out         The print writer to which the generated code is written.
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