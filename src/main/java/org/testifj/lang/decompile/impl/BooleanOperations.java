package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.*;
import org.testifj.util.Lists;
import org.testifj.util.Pair;
import org.testifj.util.Priority;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.testifj.lang.classfile.ByteCode.*;
import static org.testifj.lang.decompile.DecompilationContextQueries.lastDecompiledStatement;
import static org.testifj.lang.decompile.DecompilationStateSelectors.elementIsStacked;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.ModelQueries.assignedValue;
import static org.testifj.lang.model.ModelQueries.assignedVariableTypeIs;
import static org.testifj.lang.model.ModelQueries.equalTo;
import static org.testifj.util.Lists.optionallyCollect;
import static org.testifj.util.Lists.zip;

public final class BooleanOperations implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfiguration.Builder configurationBuilder) {
        configurationBuilder.after(integerStoreInstructions())
                .withPriority(Priority.LOW)
                .then(coerceAssignedIntegerToBoolean());

        configurationBuilder.after(invokeinterface, invokespecial, invokestatic, invokevirtual)
                .when(elementIsStacked(ElementType.METHOD_CALL))
                .then(coerceConstantIntegerMethodParameterToBoolean());
    }

    private static DecompilerDelegate coerceAssignedIntegerToBoolean() {
        final ModelQuery<DecompilationContext, VariableAssignment> query = lastDecompiledStatement().as(VariableAssignment.class)
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
}
