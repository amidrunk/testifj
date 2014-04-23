package org.testifj.lang.model;

import org.junit.Test;
import org.testifj.lang.model.impl.ConstantImpl;
import org.testifj.lang.model.impl.MethodCallImpl;
import org.testifj.lang.model.impl.MethodSignature;
import org.testifj.lang.model.impl.NewInstanceImpl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.*;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class ASTTest {

    @Test
    public void constantStringCannotBeNull() {
        expect(() -> constant(null)).toThrow(AssertionError.class);
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
        expect(() -> AST.set(null, constant("foo"))).toThrow(AssertionError.class);
        expect(() -> AST.set("", constant("foo"))).toThrow(AssertionError.class);
        expect(() -> AST.set("foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void setShouldCreateNewVariableAssignment() {
        given(AST.set("str", constant("foo"))).then(va -> {
            expect(va.getVariableName()).toBe("str");
            expect(va.getValue()).toBe(constant("foo"));
            expect(va.getVariableType()).toBe(String.class);
        });
    }

    @Test
    public void setShouldNotAcceptNullVariableType() {
        expect(() -> set("foo", null, constant(1234))).toThrow(AssertionError.class);
    }

    @Test
    public void variableAssignmentCanBeCreatedWithExplicitVariableType() {
        given(set("str", boolean.class, constant(1234))).then(expr -> {
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

}
