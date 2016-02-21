package org.testifj.lang.model;

import org.junit.Test;
import org.testifj.lang.model.impl.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.*;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class ASTTest {

    private final Expression operand1 = mock(Expression.class, "operand1");

    private final Expression operand2 = mock(Expression.class, "operand2");

    @Test
    public void constantStringCannotBeNull() {
        expect(() -> constant((String) null)).toThrow(AssertionError.class);
    }

    @Test
    public void stringConstantCanBeCreated() {
        expect(constant("foo")).toBe(new ConstantImpl("foo", String.class));
    }

    @Test
    public void intConstantCanBeCreated() {
        expect(constant(1)).toBe(new ConstantImpl(1, int.class));
    }

    @Test
    public void newInstanceShouldNotAcceptInvalidArguments() {
        expect(() -> AST.newInstance(null)).toThrow(AssertionError.class);
        expect(() -> AST.newInstance(String.class, mock(Expression.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void newInstanceShouldReturnNewInstanceExpressionWithResolvedConstructor() {
        given(AST.newInstance(String.class, AST.constant("Hello World!"))).then(it -> {
            expect(it).toBe(
                    new NewInstanceImpl(String.class, MethodSignature.parse("(Ljava/lang/String;)V"),
                            Arrays.asList(new ConstantImpl("Hello World!", String.class))));
        });
    }

    @Test
    public void setShouldNotAcceptInvalidParameters() {
        expect(() -> AST.set(-1, "foo", constant("foo"))).toThrow(AssertionError.class);
        expect(() -> AST.set(0, null, constant("foo"))).toThrow(AssertionError.class);
        expect(() -> AST.set(0, "", constant("foo"))).toThrow(AssertionError.class);
        expect(() -> AST.set(0, "foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void setShouldCreateNewVariableAssignment() {
        given(AST.set(0, "str", constant("foo"))).then(va -> {
            expect(va.getVariableName()).toBe("str");
            expect(va.getValue()).toBe(constant("foo"));
            expect(va.getVariableType()).toBe(String.class);
        });
    }

    @Test
    public void setShouldNotAcceptNullVariableType() {
        expect(() -> set(0, "foo", null, constant(1234))).toThrow(AssertionError.class);
    }

    @Test
    public void variableAssignmentCanBeCreatedWithExplicitVariableType() {
        given(set(0, "str", boolean.class, constant(1234))).then(expr -> {
            expect(expr.getVariableName()).toBe("str");
            expect(expr.getVariableType()).toBe(boolean.class);
            expect(expr.getValue()).toBe(constant(1234));
        });
    }

    @Test
    public void longConstantCanBeCreated() {
        given(constant(100L)).then(constant -> {
            expect(constant.getType()).toBe(long.class);
            expect(constant.getConstant()).toBe(100L);
        });
    }

    @Test
    public void floatConstantCanBeCreated() {
        given(constant(1234f)).then(constant -> {
            expect(constant.getType()).toBe(float.class);
            expect(constant.getConstant()).toBe(1234f);
        });
    }

    @Test
    public void doubleConstantCanBeCreated() {
        given(constant(1234d)).then(constant -> {
            expect(constant.getType()).toBe(double.class);
            expect(constant.getConstant()).toBe(1234d);
        });
    }

    @Test
    public void returnStatementCanBeCreated() {
        given($return()).then(ret -> {
            expect(ret.getElementType()).toBe(ElementType.RETURN);
        });
    }

    @Test
    public void eqShouldNotAcceptInvalidParameters() {
        expect(() -> AST.eq(null, constant("foo"))).toThrow(AssertionError.class);
        expect(() -> AST.eq(constant("foo"), null)).toThrow(AssertionError.class);
    }

    @Test
    public void eqShouldCreateBinaryExpression() {
        given(AST.eq(constant("foo"), constant("bar"))).then(e -> {
            expect(e.getLeftOperand()).toBe(constant("foo"));
            expect(e.getRightOperand()).toBe(constant("bar"));
            expect(e.getElementType()).toBe(ElementType.BINARY_OPERATOR);
            expect(e.getOperatorType()).toBe(OperatorType.EQ);
            expect(e.getType()).toBe(boolean.class);
        });
    }

    @Test
    public void callShouldNotAcceptInvalidParameters() {
        expect(() -> call((Expression) null, "foo", String.class, constant(1))).toThrow(AssertionError.class);
        expect(() -> call(constant("str"), null, String.class, constant(1))).toThrow(AssertionError.class);
        expect(() -> call(constant("str"), "", String.class, constant(1))).toThrow(AssertionError.class);
        expect(() -> call(constant("str"), "foo", null, constant(1))).toThrow(AssertionError.class);
        expect(() -> call(constant("str"), "foo", String.class, (Expression[]) null)).toThrow(AssertionError.class);
        expect(() -> call(constant("str"), "foo", String.class, constant(1), null)).toThrow(AssertionError.class);
    }

    @Test
    public void callShouldCreateNewMethodCall() {
        given(call(constant("str"), "substring", String.class, constant(0), constant(1))).then(e -> {
            expect(e.getTargetInstance()).toBe(constant("str"));
            expect(e.getMethodName()).toBe("substring");
            expect(e.getTargetType()).toBe(String.class);
            expect(e.getSignature().toString()).toBe("(II)Ljava/lang/String;");
            expect(e.getParameters().toArray()).toBe(new Object[]{constant(0), constant(1)});
        });
    }

    @Test
    public void getShouldNotAcceptInvalidParameters() {
        expect(() -> field((Type) null, String.class, "foo")).toThrow(AssertionError.class);
        expect(() -> field(String.class, null, "foo")).toThrow(AssertionError.class);
        expect(() -> field(String.class, String.class, null)).toThrow(AssertionError.class);
        expect(() -> field(String.class, String.class, "")).toThrow(AssertionError.class);
    }

    @Test
    public void getShouldCreateNewFieldReference() {
        given(field(String.class, BigDecimal.class, "foo")).then(field -> {
            expect(field.getTargetInstance()).not().toBe(present());
            expect(field.getDeclaringType()).toBe(String.class);
            expect(field.getType()).toBe(BigDecimal.class);
            expect(field.getFieldName()).toBe("foo");
        });
    }

    @Test
    public void localShouldNotAcceptInvalidArguments() {
        expect(() -> local(null, String.class, 0)).toThrow(AssertionError.class);
        expect(() -> local("", String.class, 0)).toThrow(AssertionError.class);
        expect(() -> local("foo", null, 0)).toThrow(AssertionError.class);
        expect(() -> local("foo", String.class, -1)).toThrow(AssertionError.class);
    }

    @Test
    public void localShouldCreateNotLocalVariableReference() {
        given(local("str", String.class, 1)).then(local -> {
            expect(local.getName()).toBe("str");
            expect(local.getType()).toBe(String.class);
            expect(local.getIndex()).toBe(1);
        });
    }

    @Test
    public void getInstanceFieldShouldNotAcceptInvalidArguments() {
        expect(() -> AST.field((Expression) null, String.class, "foo")).toThrow(AssertionError.class);
        expect(() -> AST.field(constant("str"), null, "foo")).toThrow(AssertionError.class);
        expect(() -> AST.field(constant("str"), String.class, null)).toThrow(AssertionError.class);
        expect(() -> AST.field(constant("str"), String.class, "")).toThrow(AssertionError.class);
    }

    @Test
    public void getInstanceFieldShouldCreateFieldReference() {
        given(AST.field(constant("str"), int.class, "length")).then(str -> {
            expect(str.getTargetInstance()).toBe(present());
            expect(str.getTargetInstance().get()).toBe(AST.constant("str"));
            expect(str.getDeclaringType()).toBe(String.class);
            expect(str.getFieldName()).toBe("length");
            expect(str.getFieldType()).toBe(int.class);
        });
    }

    @Test
    public void returnValueShouldNotAcceptNullValue() {
        expect(() -> $return(null)).toThrow(AssertionError.class);
    }

    @Test
    public void returnValueShouldCreateNewReturnValue() {
        given($return(constant(100))).then(e -> {
            expect(e.getValue()).toBe(constant(100));
        });
    }

    @Test
    public void plusShouldNotAcceptInvalidArguments() {
        expect(() -> AST.plus(null, constant(1))).toThrow(AssertionError.class);
        expect(() -> AST.plus(constant(1), null)).toThrow(AssertionError.class);
    }

    @Test
    public void plusShouldNotAcceptOperandsOfDifferentTypes() {
        expect(() -> AST.plus(constant(1d), constant(1))).toThrow(AssertionError.class);
    }

    @Test
    public void plusShouldCreateBinaryOperator() {
        given(AST.plus(constant(1), constant(2))).then(operator -> {
            expect(operator.getLeftOperand()).toBe(constant(1));
            expect(operator.getRightOperand()).toBe(constant(2));
            expect(operator.getOperatorType()).toBe(OperatorType.PLUS);
            expect(operator.getType()).toBe(int.class);
        });
    }

    @Test
    public void staticMethodCallShouldNotAcceptInvalidParameters() {
        expect(() -> AST.call((Type) null, "foo", String.class)).toThrow(AssertionError.class);
        expect(() -> AST.call(String.class, null, String.class)).toThrow(AssertionError.class);
        expect(() -> AST.call(String.class, "", String.class)).toThrow(AssertionError.class);
        expect(() -> AST.call(String.class, "foo", (Type) null)).toThrow(AssertionError.class);
        expect(() -> AST.call(String.class, "foo", String.class, (Expression) null)).toThrow(AssertionError.class);
    }

    @Test
    public void staticMethodCallShouldCreateSyntaxTreeForCall() {
        final MethodCall actualMethodCall = AST.call(String.class, "valueOf", String.class, constant(1));
        final MethodCallImpl expectedMethodCall = new MethodCallImpl(String.class, "valueOf",
                MethodSignature.parse("(I)Ljava/lang/String;"), null, new Expression[]{new ConstantImpl(1, int.class)});

        expect(actualMethodCall).toBe(expectedMethodCall);
    }

    @Test
    public void callStaticWithSignatureShouldNotAcceptInvalidParameters() {
        final Signature signature = MethodSignature.parse("(I)I");

        expect(() -> AST.call((Type) null, "foo", signature)).toThrow(AssertionError.class);
        expect(() -> AST.call(String.class, "", signature)).toThrow(AssertionError.class);
        expect(() -> AST.call(String.class, null, signature)).toThrow(AssertionError.class);
        expect(() -> AST.call(String.class, "foo", (Signature) null)).toThrow(AssertionError.class);
        expect(() -> AST.call(String.class, "foo", signature, (Expression) null)).toThrow(AssertionError.class);
    }

    @Test
    public void callStaticWithSignatureShouldCreateMethodCall() {
        final MethodSignature signature = MethodSignature.parse("(Z)Ljava/lang/Boolean;");
        final MethodCall methodCall = AST.call(Boolean.class, "valueOf", signature, constant(1));

        expect(methodCall).toBe(new MethodCallImpl(Boolean.class, "valueOf", signature, null, new Expression[]{constant(1)}));
    }

    @Test
    public void castShouldNotAcceptNullExpressionOrType() {
        expect(() -> AST.cast(null)).toThrow(AssertionError.class);
        expect(() -> AST.cast(constant("foo")).to(null)).toThrow(AssertionError.class);
    }

    @Test
    public void castShouldCreateNewCast() {
        given(cast(constant("foo")).to(String.class)).then(cast -> {
            expect(cast.getValue()).toBe(constant("foo"));
            expect(cast.getType()).toBe(String.class);
        });
    }

    @Test
    public void classConstantCannotHaveNullClass() {
        expect(() -> AST.constant((Class) null)).toThrow(AssertionError.class);
    }

    @Test
    public void classConstantCanBeCreated() {
        expect(AST.constant(String.class)).toBe(new ConstantImpl(String.class, Class.class));
    }

    @Test
    public void addShouldNotAcceptAnyNullOperandOrResultType() {
        expect(() -> AST.add(null, mock(Expression.class), Integer.class)).toThrow(AssertionError.class);
        expect(() -> AST.add(mock(Expression.class), null, Integer.class)).toThrow(AssertionError.class);
        expect(() -> AST.add(mock(Expression.class), mock(Expression.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void addShouldCreateBinaryOperatorWithOperands() {
        given(AST.add(operand1, operand2, Integer.class)).then(it -> {
            expect(it.getLeftOperand()).toBe(operand1);
            expect(it.getRightOperand()).toBe(operand2);
            expect(it.getType()).toBe(Integer.class);
            expect(it.getOperatorType()).toBe(OperatorType.PLUS);
        });
    }

    @Test
    public void subShouldCreateBinaryOperatorWithOperands() {
        given(AST.sub(operand1, operand2, Integer.class)).then(it -> {
            expect(it.getLeftOperand()).toBe(operand1);
            expect(it.getRightOperand()).toBe(operand2);
            expect(it.getType()).toBe(Integer.class);
            expect(it.getOperatorType()).toBe(OperatorType.MINUS);
        });
    }

    @Test
    public void binaryOperatorShouldNotAcceptInvalidArguments() {
        expect(() -> AST.binaryOperator(null, OperatorType.PLUS, operand1, Integer.class)).toThrow(AssertionError.class);
        expect(() -> AST.binaryOperator(operand1, null, operand2, Integer.class)).toThrow(AssertionError.class);
        expect(() -> AST.binaryOperator(operand1, OperatorType.PLUS, null, Integer.class)).toThrow(AssertionError.class);
        expect(() -> AST.binaryOperator(operand1, OperatorType.PLUS, operand2, null)).toThrow(AssertionError.class);
    }

    @Test
    public void mulShouldCreateBinaryOperatorWithOperands() {
        given(AST.mul(operand1, operand2, Integer.class)).then(it -> {
            expect(it.getLeftOperand()).toBe(operand1);
            expect(it.getRightOperand()).toBe(operand2);
            expect(it.getType()).toBe(Integer.class);
            expect(it.getOperatorType()).toBe(OperatorType.MULTIPLY);
        });
    }

    @Test
    public void divShouldCreateBinaryOperatorWithOperands() {
        given(AST.div(operand1, operand2, Integer.class)).then(it -> {
            expect(it.getLeftOperand()).toBe(operand1);
            expect(it.getRightOperand()).toBe(operand2);
            expect(it.getType()).toBe(Integer.class);
            expect(it.getOperatorType()).toBe(OperatorType.DIVIDE);
        });
    }

    @Test
    public void setLocalShouldNotAcceptNullVariable() {
        expect(() -> AST.set(null)).toThrow(AssertionError.class);
    }

    @Test
    public void setLocalShouldNotAcceptNullValue() {
        expect(() -> AST.set(mock(LocalVariableReference.class)).to(null)).toThrow(AssertionError.class);
    }

    @Test
    public void setLocalShouldCreateVariableAssignment() {
        final LocalVariableReference local = new LocalVariableReferenceImpl("foo", String.class, 1234);
        final Expression expression = mock(Expression.class);
        final VariableAssignment variableAssignment = AST.set(local).to(expression);

        expect(variableAssignment.getValue()).toBe(expression);
        expect(variableAssignment.getVariableIndex()).toBe(1234);
        expect(variableAssignment.getVariableName()).toBe("foo");
        expect(variableAssignment.getVariableType()).toBe(String.class);
    }

    @Test
    public void booleanConstantCanBeCreated() {
        expect(constant(true)).toBe(new ConstantImpl(true, boolean.class));
        expect(constant(false)).toBe(new ConstantImpl(false, boolean.class));
    }

    @Test
    public void modShouldReturnModuloOperation() {
        expect(AST.mod(constant(1), constant(2), int.class)).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.MODULO,
                constant(2),
                int.class));
    }

    @Test
    public void lshiftShouldReturnBinaryLeftShiftOperator() {
        expect(AST.lshift(constant(1), constant(2), int.class)).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.LSHIFT,
                constant(2),
                int.class));
    }

    @Test
    public void rshiftShouldReturnBinaryRightShiftOperator() {
        expect(AST.rshift(constant(1), constant(2), int.class)).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.RSHIFT,
                constant(2),
                int.class));
    }

    @Test
    public void unsignedRshiftShouldReturnBinaryRightShiftOperator() {
        expect(AST.unsignedRightShift(constant(1), constant(2), int.class)).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.UNSIGNED_RSHIFT,
                constant(2),
                int.class));
    }

    @Test
    public void neShouldCreateBinaryOperatorWithNEOperator() {
        expect(AST.ne(constant(1), constant(2))).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.NE,
                constant(2),
                boolean.class));
    }

    @Test
    public void eqShouldCreateBinaryOperatorWithEQOperator() {
        expect(AST.eq(constant(1), constant(2))).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.EQ,
                constant(2),
                boolean.class));
    }

    @Test
    public void geShouldCreateBinaryOperatorWithGEOperator() {
        expect(AST.ge(constant(1), constant(2))).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.GE,
                constant(2),
                boolean.class));
    }

    @Test
    public void gtShouldCreateBinaryOperatorWithGTOperator() {
        expect(AST.gt(constant(1), constant(2))).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.GT,
                constant(2),
                boolean.class));
    }

    @Test
    public void leShouldCreateBinaryOperatorWithLEOperator() {
        expect(AST.le(constant(1), constant(2))).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.LE,
                constant(2),
                boolean.class));
    }

    @Test
    public void ltShouldCreateBinaryOperatorWithLTOperator() {
        expect(AST.lt(constant(1), constant(2))).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.LT,
                constant(2),
                boolean.class));
    }

    @Test
    public void andShouldCreateBinaryOperatorWithAndOperator() {
        expect(AST.and(constant(1), constant(2))).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.AND,
                constant(2),
                boolean.class));
    }

    @Test
    public void orShouldCreateBinaryOperatorWithOrOperator() {
        expect(AST.or(constant(1), constant(2))).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.OR,
                constant(2),
                boolean.class));
    }

    @Test
    public void bitwiseAndShouldCreateBinaryOperatorWithAndOperator() {
        expect(AST.bitwiseAnd(constant(1), constant(2), int.class)).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.BITWISE_AND,
                constant(2),
                int.class));
    }

    @Test
    public void bitwiseOrShouldCreateBinaryOperatorWithOrOperator() {
        expect(AST.bitwiseOr(constant(1), constant(2), int.class)).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.BITWISE_OR,
                constant(2),
                int.class));
    }

    @Test
    public void xorShouldCreateBinaryOperatorWithXorOperator() {
        expect(AST.xor(constant(1), constant(2), int.class)).toBe(new BinaryOperatorImpl(
                constant(1),
                OperatorType.XOR,
                constant(2),
                int.class));
    }

    @Test
    public void newArrayShouldCreateArray() {
        final NewArray array = AST.newArray(int[].class, constant(1), constant(2));

        assertEquals(int[].class, array.getType());
        assertEquals(int.class, array.getComponentType());
        assertEquals(constant(2), array.getLength());
        assertEquals(constant(1), array.getInitializers().get(0).getValue());
        assertEquals(constant(2), array.getInitializers().get(1).getValue());
    }

}
