package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.BinaryOperatorImpl;
import org.testifj.lang.model.impl.BranchImpl;
import org.testifj.lang.model.impl.CompareImpl;
import org.testifj.lang.model.impl.UnaryOperatorImpl;
import org.testifj.util.Lists;
import org.testifj.util.Pair;
import org.testifj.util.Priority;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.testifj.lang.classfile.ByteCode.*;
import static org.testifj.lang.decompile.DecompilationContextQueries.lastStatement;
import static org.testifj.lang.decompile.DecompilationContextQueries.secondToLastStatement;
import static org.testifj.lang.decompile.DecompilationStateSelectors.*;
import static org.testifj.lang.decompile.DecompilerDelegates.forQuery;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.ModelQueries.*;
import static org.testifj.util.Lists.optionallyCollect;
import static org.testifj.util.Lists.zip;

public final class BooleanOperations implements DecompilerDelegation {

    private final ModelQuery<DecompilationContext, Branch> branchIfOperatorIsNotZero = secondToLastStatement()
            .as(Branch.class)
            .where(rightComparativeOperand().is(equalTo(constant(0))))
                .and(operatorTypeIs(OperatorType.NE))
                .and(leftComparativeOperand().is(ofRuntimeType(boolean.class)));

    @Override
    public void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.lcmp).then(lcmp());
        configurationBuilder.on(ByteCode.fcmpl).then(fcmpl());
        configurationBuilder.on(ByteCode.fcmpg).then(fcmpg());
        configurationBuilder.on(ByteCode.dcmpl).then(dcmpl());
        configurationBuilder.on(ByteCode.dcmpg).then(dcmpg());
        configurationBuilder.on(ByteCode.ifne).then(ifne());
        configurationBuilder.on(ByteCode.ifeq).then(ifeq());
        configurationBuilder.on(ByteCode.iflt).then(iflt());
        configurationBuilder.on(ByteCode.ifge).then(ifge());
        configurationBuilder.on(ByteCode.ifgt).then(ifgt());
        configurationBuilder.on(ByteCode.ifle).then(ifle());
        configurationBuilder.on(ByteCode.if_icmpne).then(if_icmpne());
        configurationBuilder.on(ByteCode.if_icmpeq).then(if_icmpeq());
        configurationBuilder.on(ByteCode.if_icmpge).then(if_icmpge());
        configurationBuilder.on(ByteCode.if_icmpgt).then(if_icmpgt());
        configurationBuilder.on(ByteCode.if_icmple).then(if_icmple());
        configurationBuilder.on(ByteCode.if_icmplt).then(if_icmplt());
        configurationBuilder.on(ByteCode.if_acmpeq).then(if_acmpeq());
        configurationBuilder.on(ByteCode.if_acmpne).then(if_acmpne());

        configurationBuilder.after(ByteCode.iconst_0)
                .when(lastStatementIs(ElementType.GOTO).and(elementsAreStacked(constant(1), constant(0))))
                .then(forQuery(branchIfOperatorIsNotZero).apply(invertBoolean()));

        configurationBuilder.after(ByteCode.iconst_0)
                .when(lastStatementIs(ElementType.GOTO).and(elementsAreStacked(constant(1), constant(0))))
                .then(forQuery(secondToLastStatement().as(Branch.class)).apply(binaryBranchToBooleanCompare()));

        configurationBuilder.after(integerStoreInstructions())
                .withPriority(Priority.LOW)
                .then(coerceAssignedIntegerToBoolean());

        configurationBuilder.after(invokeinterface, invokespecial, invokestatic, invokevirtual)
                .when(elementIsStacked(ElementType.METHOD_CALL))
                .then(coerceConstantIntegerMethodParameterToBoolean());
    }

    public static DecompilerDelegate lcmp() {
        return cmp();
    }

    public static DecompilerDelegate fcmpl() {
        return cmp();
    }

    public static DecompilerDelegate fcmpg() {
        return cmp();
    }

    public static DecompilerDelegate dcmpl() {
        return cmp();
    }

    public static DecompilerDelegate dcmpg() {
        return cmp();
    }

    private static DecompilerDelegate cmp() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression rightOperand = context.getStack().pop();
                final Expression leftOperand = context.getStack().pop();

                context.getStack().push(new CompareImpl(leftOperand, rightOperand));
            }
        };
    }

    public static DecompilerDelegate if_icmpne() {
        return if_cmp(OperatorType.NE);
    }

    public static DecompilerDelegate if_icmpeq() {
        return if_cmp(OperatorType.EQ);
    }

    public static DecompilerDelegate if_icmpge() {
        return if_cmp(OperatorType.GE);
    }

    public static DecompilerDelegate if_icmpgt() {
        return if_cmp(OperatorType.GT);
    }

    public static DecompilerDelegate if_icmple() {
        return if_cmp(OperatorType.LE);
    }

    public static DecompilerDelegate if_icmplt() {
        return if_cmp(OperatorType.LT);
    }

    public static DecompilerDelegate if_acmpeq() {
        return if_cmp(OperatorType.EQ);
    }

    public static DecompilerDelegate if_acmpne() {
        return if_cmp(OperatorType.NE);
    }

    public static DecompilerDelegate ifne() {
        return ifcmp0(OperatorType.NE);
    }

    public static DecompilerDelegate ifeq() {
        return ifcmp0(OperatorType.EQ);
    }

    public static DecompilerDelegate iflt() {
        return ifcmp0(OperatorType.LT);
    }

    public static DecompilerDelegate ifge() {
        return ifcmp0(OperatorType.GE);
    }

    public static DecompilerDelegate ifgt() {
        return ifcmp0(OperatorType.GT);
    }

    public static DecompilerDelegate ifle() {
        return ifcmp0(OperatorType.LE);
    }

    private static DecompilerDelegate if_cmp(OperatorType operatorType) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final int targetPC = context.getProgramCounter().get() + codeStream.nextUnsignedShort();
                final Expression rightOperand = context.pop();
                final Expression leftOperand = context.pop();

                context.enlist(new BranchImpl(leftOperand, operatorType, rightOperand, targetPC));
            }
        };
    }

    private static DecompilerDelegate coerceAssignedIntegerToBoolean() {
        final ModelQuery<DecompilationContext, VariableAssignment> query = lastStatement().as(VariableAssignment.class)
                .where(assignedVariableTypeIs(boolean.class))
                .and(assignedValue().is(equalTo(constant(0)).or(equalTo(constant(1)))));

        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Optional<VariableAssignment> result = query.from(context);

                if (result.isPresent()) {
                    final VariableAssignment variableAssignment = result.get().as(VariableAssignment.class);

                    if (variableAssignment.getVariableType().equals(boolean.class)) {
                        if (variableAssignment.getValue().equals(constant(1))) {
                            context.getStatements().last().swap(variableAssignment.withValue(constant(true)));
                        } else if (variableAssignment.getValue().equals(constant(0))) {
                            context.getStatements().last().swap(variableAssignment.withValue(constant(false)));
                        }
                    }
                }
            }
        };
    }

    private static DecompilerDelegate coerceConstantIntegerMethodParameterToBoolean() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final MethodCall methodCall = context.peek().as(MethodCall.class);

                final Optional<List<Pair<Type, Expression>>> newArgs = optionallyCollect(zip(methodCall.getSignature().getParameterTypes(), methodCall.getParameters()), typeAndValue -> {
                    if (typeAndValue.left().equals(boolean.class) && typeAndValue.right().getType().equals(int.class)) {
                        if (typeAndValue.right().equals(constant(1))) {
                            return typeAndValue.right(constant(true));
                        } else if (typeAndValue.right().equals(constant(0))) {
                            return typeAndValue.right(constant(false));
                        }
                    }

                    return typeAndValue;
                });

                if (newArgs.isPresent()) {
                    context.getStack().swap(methodCall.withParameters(Lists.collect(newArgs.get(), Pair::right)));
                }
            }
        };
    }

    private DecompilerTransformation<Branch> binaryBranchToBooleanCompare() {
        return (context, codeStream, byteCode, result) -> {
            context.getStack().pop();
            context.getStack().pop();
            context.getStatements().tail(-2).remove();

            final Expression leftOperand;
            final Expression rightOperand;

            if (result.getLeftOperand().getElementType() == ElementType.COMPARE && result.getRightOperand().equals(constant(0))) {
                final Compare compare = result.getLeftOperand().as(Compare.class);

                leftOperand = compare.getLeftOperand();
                rightOperand = compare.getRightOperand();
            } else {
                leftOperand = result.getLeftOperand();
                rightOperand = result.getRightOperand();
            }

            final OperatorType operatorType;

            switch (result.getOperatorType()) {
                case EQ:
                    operatorType = OperatorType.NE;
                    break;
                case NE:
                    operatorType = OperatorType.EQ;
                    break;
                case GE:
                    operatorType = OperatorType.LT;
                    break;
                case GT:
                    operatorType = OperatorType.LE;
                    break;
                case LE:
                    operatorType = OperatorType.GT;
                    break;
                case LT:
                    operatorType = OperatorType.GE;
                    break;
                default:
                    throw new UnsupportedOperationException("Can't transform operator " + result.getOperatorType());
            }

            context.push(new BinaryOperatorImpl(leftOperand, operatorType, rightOperand, boolean.class));
        };
    }

    private static DecompilerTransformation<Branch> invertBoolean() {
        return (context, codeStream, byteCode, result) -> {
            context.getStack().pop();
            context.getStack().pop();
            context.getStatements().tail(-2).remove();
            context.push(new UnaryOperatorImpl(result.getLeftOperand(), OperatorType.NOT, boolean.class));
        };
    }

    private static DecompilerDelegate ifcmp0(final OperatorType operatorType) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                context.enlist(new BranchImpl(context.getStack().pop(), operatorType, constant(0), codeStream.nextSignedShort()));
            }
        };
    }
}
