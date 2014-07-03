package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerDelegate;
import org.testifj.lang.decompile.DecompilerDelegation;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;
import org.testifj.lang.model.VariableAssignment;
import org.testifj.util.Lists;
import org.testifj.util.Pair;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.testifj.lang.decompile.DecompilationStateSelectors.elementIsStacked;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.util.Lists.optionallyCollect;
import static org.testifj.util.Lists.zip;

public final class BooleanOperations implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfiguration.Builder configurationBuilder) {
        /*configurationBuilder.after(ByteCode.integerStoreInstructions())
                .then((context,codeStream,byteCode) -> {
                    final VariableAssignment variableAssignment = Lists.last(context.getStatements()).get().as(VariableAssignment.class);

                    if (variableAssignment.getVariableType().equals(boolean.class)) {
                        if (variableAssignment.getValue().equals(constant(1))) {

                        } else if (variableAssignment.getValue().equals(constant(0))) {

                        }
                    }
                });*/

        configurationBuilder.after(ByteCode.invokeinterface, ByteCode.invokespecial, ByteCode.invokestatic, ByteCode.invokevirtual)
                .when(elementIsStacked(ElementType.METHOD_CALL))
                .then(coerceConstantIntegerMethodParameterToBoolean());
    }

    private DecompilerDelegate coerceConstantIntegerMethodParameterToBoolean() {
        return (context, codeStream, byteCode) -> {
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
        };
    }
}
