package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.LocalVariable;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.ConstantImpl;
import org.testifj.lang.model.impl.IncrementImpl;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;

import java.io.IOException;
import java.util.Optional;

import static org.testifj.lang.decompile.DecompilationContextQueries.*;
import static org.testifj.lang.decompile.DecompilationStateSelectors.atLeastOneStatement;
import static org.testifj.lang.decompile.DecompilationStateSelectors.elementIsStacked;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.ModelQueries.*;

public final class UnaryOperations implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfiguration.Builder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompilation configuration builder can't be null";


        decompilerConfigurationBuilder.after(ByteCode.primitiveLoadInstructions())
                .when(stackContainsPrefixIncrementOfVariable())
                .then(correctPrefixIncrement());

        decompilerConfigurationBuilder.after(ByteCode.iinc)
                .when(stackContainsPostfixIncrementOfVariable())
                .then(correctPostfixIncrement());

        decompilerConfigurationBuilder.after(ByteCode.integerLoadInstructions())
                .when(atLeastOneStatement())
                .then(correctPrefixByteIncrement());

        decompilerConfigurationBuilder.after(ByteCode.integerStoreInstructions())
                .when(atLeastOneStatement().and(elementIsStacked(ElementType.VARIABLE_REFERENCE)))
                .then(correctPostfixByteCodeIncrement());

        decompilerConfigurationBuilder.on(ByteCode.iinc).then(iinc());
    }

    /**
     * Returns a decompiler delegate that handles the iinc operation. Note that iinc increments a variable without
     * leaving anything on the stack. Further, it does not state whether the increment is prefix or postfix. To fully
     * implement the increment, the stack will need to be corrected afterwards.
     *
     * @see UnaryOperations#correctPostfixIncrement()
     * @see UnaryOperations#correctPrefixIncrement()
     * @see UnaryOperations#correctPrefixByteIncrement()
     * @return A decompiler delegate that handles the <code>iinc=132</code> instruction.
     */
    public static DecompilerDelegate iinc() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final int variableIndex = codeStream.nextUnsignedByte();
                final int value = codeStream.nextByte();
                final LocalVariable localVariable = context.getMethod().getLocalVariableForIndex(variableIndex);

                context.push(new IncrementImpl(
                        new LocalVariableReferenceImpl(
                                localVariable.getName(),
                                localVariable.getType(),
                                variableIndex),
                        new ConstantImpl(value, int.class),
                        int.class,
                        Affix.UNDEFINED
                ));
            }
        };
    }

    /**
     * Returns a decompiler delegate that corrects a post fix increment s.t. that stack is transformed according to
     * <code>[X, increment(var=X, affix=undefined)] => [increment(var=X, affix=POSTFIX)]</code>
     *
     * @return A decompiler delegate that corrects a post fix increment.
     */
    private DecompilerDelegate correctPostfixIncrement() {
        return (context, codeStream, byteCode) -> {
            final Increment increment = context.getStack().pop().as(Increment.class);

            context.getStack().swap(new IncrementImpl(
                    increment.getLocalVariable(),
                    increment.getValue(),
                    int.class, Affix.POSTFIX));
        };
    }

    /**
     * Returns a decompiler delegate that corrects a prefix increment s.t. the stack is transformed according to
     * <code>[increment(var=X, affix=undefined), X] => [increment(var=X, affix=PREFIX)]</code>.
     *
     *
     * @return A decompiler delegate that corrects a prefix increment.
     */
    private DecompilerDelegate correctPrefixIncrement() {
        return (context, codeStream, byteCode) -> {
            final LocalVariableReference localVariableReference = context.pop().as(LocalVariableReference.class);
            final Increment increment = context.peek().as(Increment.class);

            context.getStack().swap(new IncrementImpl(localVariableReference, increment.getValue(), int.class, Affix.PREFIX));
        };
    }

    /**
     * Returns a selector that matches an uncorrected postfix increment.
     *
     * @return A selector that matches <code>[X, increment(variable=X, affix=undefined)]</code>.
     */
    private DecompilationStateSelector stackContainsPostfixIncrementOfVariable() {
        final ModelQuery<DecompilationContext, Increment> query = currentValue()
                .as(Increment.class)
                .where(affixIsUndefined());

        return (context, byteCode) -> {
            final Optional<Increment> increment = query.from(context);
            final Optional<Expression> localVariable = previousValue().from(context);

            return increment.isPresent()
                    && localVariable.isPresent()
                    && increment.get().getLocalVariable().equals(localVariable.get());
        };
    }

    /**
     * Returns a selector that matches an uncorrected prefix increment.
     *
     * @return A selector that matches <code>[increment(variable=X, affix=undefined), X]</code>.
     */
    private DecompilationStateSelector stackContainsPrefixIncrementOfVariable() {
        final ModelQuery<DecompilationContext, Increment> query = previousValue()
                .as(Increment.class)
                .where(affixIsUndefined());

        return (context, byteCode) -> {
            final Optional<Increment> optionalIncrement = query.from(context);

            return optionalIncrement.isPresent() && optionalIncrement.get().getLocalVariable().equals(context.peek());

        };
    }

    /**
     * Corrects the compiled prefix increment on a byte. The compiled code will not use the increment operation
     * but will rather add/subtract 1 from the variable and then reload it. Example:
     * <pre>{@code
     * iload_1      [myByteVariable]
     * iconst_1     [myByteVariable, 1]
     * isub         [myByteVariable - 1]
     * l2b          [(byte)(myByteVariable - 1)]
     * istore_1     []
     * iload_1      [myByteVariable]
     * }
     * </pre>
     *
     * @return A decompiler delegate that corrects the syntax tree for byte prefix increment/decrement.
     */
    private static DecompilerDelegate correctPrefixByteIncrement() {
        return (context, codeStream, byteCode) -> {
            final LocalVariableReference loadedVariable = context.peek().as(LocalVariableReference.class);

            final Optional<BinaryOperator> increment = lastDecompiledStatement().as(VariableAssignment.class)
                    .where(isAssignmentTo(loadedVariable))
                    .get(assignedValue()).as(Cast.class).where(isCastTo(byte.class))
                    .get(castValue()).as(BinaryOperator.class)
                    .where(leftOperand().is(equalTo(loadedVariable)))
                    .and(rightOperand().as(Constant.class).is(equalTo(constant(1))))
                    .from(context);

            if (!increment.isPresent()) {
                return;
            }

            context.removeStatement(context.getStatements().size() - 1);
            context.pop();

            context.push(new IncrementImpl(loadedVariable, constant(increment.get().getOperatorType() == OperatorType.MINUS ? -1 : 1), byte.class, Affix.PREFIX));
        };
    }

    /**
     * Corrects a postfix byte increment. A byte increment does not use the iinc operator since that results in
     * an integer (byte newByte = otherByte++ was not syntactically correct in previous java versions). This delegate
     * will change the following sequence to a postfix increment/decrement:
     * <pre>{@code
     * iload_1
     * iload_1      [var, var]
     * iconst_1     [var, var, 1]
     * isub         [var, var - 1]
     * i2b          [var, (byte)(var - 1)]
     * istore_1     [var]
     * }</pre>
     *
     * @return A decompiler delegate that corrects postfix byte increment.
     */
    private DecompilerDelegate correctPostfixByteCodeIncrement() {
        return (context, codeStream, byteCode) -> {
            final Optional<BinaryOperator> result = lastDecompiledStatement().as(VariableAssignment.class)
                    .get(assignedValue()).as(Cast.class).where(isCastTo(byte.class))
                    .get(castValue()).as(BinaryOperator.class)
                    .where(leftOperand().is(equalTo(context.getStack().peek())))
                    .and(rightOperand().is(equalTo(constant(1))))
                    .from(context);

            if (!result.isPresent()) {
                return;
            }

            context.removeStatement(context.getStatements().size() - 1);
            context.getStack().push(new IncrementImpl(
                    context.getStack().pop().as(LocalVariableReference.class),
                    constant(result.get().getOperatorType() == OperatorType.MINUS ? -1 : 1),
                    byte.class,
                    Affix.POSTFIX
            ));
        };
    }
}
