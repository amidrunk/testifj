package org.testifj.lang.model;

import org.testifj.lang.model.impl.*;

import java.lang.reflect.Type;
import java.util.Arrays;

public final class AST {

    public static Constant constant(String constant) {
        assert constant != null : "String constant can't be null";
        return new ConstantImpl(constant, String.class);
    }

    public static Constant constant(int value) {
        return new ConstantImpl(value, int.class);
    }

    public static Constant constant(long value) {
        return new ConstantImpl(value, long.class);
    }

    public static Constant constant(float value) {
        return new ConstantImpl(value, float.class);
    }

    public static Constant constant(double value) {
        return new ConstantImpl(value, double.class);
    }

    public static Return $return() {
        return new ReturnImpl();
    }

    public static NewInstance newInstance(Type type, Expression ... arguments) {
        assert type != null : "Type can't be null";
        assert arguments != null : "Arguments can't be null";

        final Type[] parameterTypes = Arrays.stream(arguments).map(attribute -> {
            assert attribute != null : "No attribute can be null";
            return attribute.getType();
        }).toArray(Type[]::new);

        final MethodSignature signature = MethodSignature.create(parameterTypes, void.class);

        return new NewInstanceImpl(type, signature, Arrays.asList(arguments));
    }

    public static VariableAssignment set(String variableName, Expression value) {
        assert value != null : "Value can't be null";

        return set(variableName, value.getType(), value);
    }

    public static VariableAssignment set(String variableName, Type variableType, Expression value) {
        assert variableName != null && !variableName.isEmpty() : "Variable name can't be null or empty";
        assert value != null : "Value can't be null";
        assert variableType != null : "Variable type can't be null";

        return new VariableAssignmentImpl(value, variableName, variableType);
    }

    public static BinaryOperator eq(Expression leftOperand, Expression rightOperand) {
        assert leftOperand != null : "Left operand can't be null";
        assert rightOperand != null : "Right operand can't be null";

        return new BinaryOperatorImpl(leftOperand, OperatorType.EQ, rightOperand, boolean.class);
    }

    public static MethodCall call(Type targetType, String methodName, Type returnType, Expression ... parameters) {
        assert targetType != null : "Target type can't be null";
        assert methodName != null : "Method name can't be null";
        assert returnType != null : "Return type can't be null";

        return new MethodCallImpl(targetType, methodName, MethodSignature.create(typesOf(parameters), returnType), null, parameters);
    }

    public static MethodCall call(Expression instance, String methodName, Type returnType, Expression ... parameters) {
        assert instance != null : "Instance can't be null";
        assert methodName != null && !methodName.isEmpty() : "Method name can't be null or empty";
        assert returnType != null : "Return type can't be null";
        assert parameters != null : "Parameters can't be null";

        final Type targetType = instance.getType();
        final Type[] parameterTypes = typesOf(parameters);

        final Signature signature = MethodSignature.create(parameterTypes, returnType);

        return new MethodCallImpl(targetType, methodName, signature, instance, parameters);
    }

    public static FieldReference field(Type declaringType, Type fieldType, String fieldName) {
        return new FieldReferenceImpl(null, declaringType, fieldType, fieldName);
    }

    public static FieldReference field(Expression instance, Type fieldType, String fieldName) {
        assert instance != null : "Instance can't be null";

        return new FieldReferenceImpl(instance, instance.getType(), fieldType, fieldName);
    }

    public static LocalVariableReference local(String variableName, Type variableType, int index) {
        return new LocalVariableReferenceImpl(variableName, variableType, index);
    }

    public static ReturnValue $return(Expression value) {
        return new ReturnValueImpl(value);
    }

    public static BinaryOperator plus(Expression leftOperand, Expression rightOperand) {
        assert leftOperand != null : "Left operand can't be null";
        assert rightOperand != null : "Right operand can't be null";
        assert leftOperand.getType().equals(rightOperand.getType()) : "Operands must be of the same type";

        return new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, leftOperand.getType());
    }

    private static Type[] typesOf(Expression[] parameters) {
        return Arrays.stream(parameters).map(e -> {
            assert e != null : "No parameter can be null";
            return e.getType();
        }).toArray(Type[]::new);
    }

}
