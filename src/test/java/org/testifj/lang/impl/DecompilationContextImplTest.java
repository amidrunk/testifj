package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.Decompiler;
import org.testifj.lang.Method;
import org.testifj.lang.TypeResolver;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;
import org.testifj.lang.model.Statement;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.CollectionThatIs.empty;

public class DecompilationContextImplTest {

    private final ProgramCounter programCounter = new ProgramCounterImpl();

    private final Decompiler decompiler = mock(Decompiler.class);

    private final Method method = mock(Method.class);

    private final TypeResolver typeResolver = mock(TypeResolver.class);

    private final DecompilationContextImpl context = new DecompilationContextImpl(decompiler, method, programCounter, typeResolver);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        expect(() -> new DecompilationContextImpl(null, method, programCounter, typeResolver)).toThrow(AssertionError.class);
        expect(() -> new DecompilationContextImpl(decompiler, null, programCounter, typeResolver)).toThrow(AssertionError.class);
        expect(() -> new DecompilationContextImpl(decompiler, method, null, typeResolver)).toThrow(AssertionError.class);
        expect(() -> new DecompilationContextImpl(decompiler, method, programCounter, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        expect(context.getDecompiler()).toBe(decompiler);
        expect(context.getMethod()).toBe(method);
        expect(context.getProgramCounter()).toBe(programCounter);
    }

    @Test
    public void resolveTypeShouldNotAcceptNullType() {
        expect(() -> context.resolveType(null)).toThrow(AssertionError.class);
        expect(() -> context.resolveType("")).toThrow(AssertionError.class);
    }

    @Test
    public void resolveTypeShouldReturnTypeFromTypeResolver() {
        when(typeResolver.resolveType(eq("java.lang.String"))).thenReturn(String.class);

        given(context.resolveType("java/lang/String")).then(type -> {
            expect(type).toBe(String.class);
        });

        verify(typeResolver).resolveType(eq("java.lang.String"));
    }

    @Test
    public void enlistedStatementsShouldInitiallyBeEmpty() {
        expect(context.getStatements()).toBe(empty());
    }

    @Test
    public void popShouldFailIfNoExpressionIsAvailableOnStack() {
        expect(context::pop).toThrow(IllegalStateException.class);
    }

    @Test
    public void popShouldReturnLastPushedExpression() {
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        context.push(expression1);
        context.push(expression2);

        expect(context.pop()).toBe(expression2);
        expect(context.pop()).toBe(expression1);
    }

    @Test
    public void pushShouldNotAcceptNullElement() {
        expect(() -> context.push(null)).toThrow(AssertionError.class);
    }

    @Test
    public void statementsFromContextCannotBeModified() {
        expect(() -> context.getStatements().add(mock(Statement.class))).toThrow(UnsupportedOperationException.class);
        expect(context.getStatements()).toBe(empty());
    }

    @Test
    public void enlistShouldNotAcceptNullArg() {
        expect(() -> context.enlist(null)).toThrow(AssertionError.class);
    }

    @Test
    public void statementsShouldContainEnlistedStatements() {
        final Statement statement1 = mock(Statement.class);
        final Statement statement2 = mock(Statement.class);

        context.enlist(statement1);
        expect(context.getStatements().toArray()).toBe(new Object[]{statement1});

        context.enlist(statement2);
        expect(context.getStatements().toArray()).toBe(new Object[]{statement1, statement2});
    }

    @Test
    public void reduceShouldReturnFalseIfStackIsEmpty() {
        expect(context.reduce()).toBe(false);
    }

    @Test
    public void reduceShouldFailIfStackContainsNonStatement() {
        context.push(mock(Expression.class));
        expect(context::reduce).toThrow(IllegalStateException.class);
    }

    @Test
    public void reduceShouldPopAndEnlistStackedStatement() {
        final Expression expression = mock(Expression.class);
        final Expression statement = mock(MethodCall.class);

        context.push(expression);
        context.push(statement);
        context.reduce();

        expect(context.getStatements().toArray()).toBe(new Object[]{statement});
        expect(context.pop()).toBe(expression);
    }

    @Test
    public void reduceAllShouldReturnFalseIfStackIsEmpty() {
        expect(context.reduceAll()).toBe(false);
    }

    @Test
    public void reduceAllShouldFailIfStackContainsNonStatement() {
        context.push(mock(Expression.class));

        expect(context::reduceAll).toThrow(IllegalStateException.class);
    }

    @Test
    public void reduceAllShouldReduceAllStatementsToStatementList() {
        final MethodCall statement1 = mock(MethodCall.class, "s1");
        final MethodCall statement2 = mock(MethodCall.class, "s2");

        context.push(statement1);
        context.push(statement2);

        expect(context.reduceAll()).toBe(true);
        expect(context.getStatements().toArray()).toBe(new Object[]{statement1, statement2});
    }

    @Test
    public void hasStackedExpressionsShouldReturnTrueIfStackContainsExpressions() {
        context.push(mock(Expression.class));
        expect(context.hasStackedExpressions()).toBe(true);
    }

    @Test
    public void hasStackedExpressionsShouldReturnFalseIfStackContainsNoExpressions() {
        expect(context.hasStackedExpressions()).toBe(false);
    }

    @Test
    public void replaceStatementShouldValidateParameters() {
        context.enlist(mock(Statement.class));

        expect(() -> context.replaceStatement(-1, mock(Statement.class))).toThrow(IndexOutOfBoundsException.class);
        expect(() -> context.replaceStatement(1, mock(Statement.class))).toThrow(IndexOutOfBoundsException.class);
        expect(() -> context.replaceStatement(0, null)).toThrow(AssertionError.class);
    }

    @Test
    public void removeStatementShouldFailForInvalidIndex() {
        expect(() -> context.removeStatement(-1)).toThrow(IndexOutOfBoundsException.class);
        expect(() -> context.removeStatement(1)).toThrow(IndexOutOfBoundsException.class);
    }

    @Test
    public void removeStatementShouldRemoveStatementAtIndex() {
        context.enlist(mock(Statement.class));
        context.removeStatement(0);

        expect(context.getStatements()).toBe(empty());
    }

    @Test
    public void reduceShouldRetainExecutionOrder() {
        final MethodCall methodCall1 = mock(MethodCall.class, "s1");
        final MethodCall methodCall2 = mock(MethodCall.class, "s2");

        context.push(methodCall1);
        context.push(methodCall2);

        context.reduce();
        context.reduceAll();

        final Statement[] expectedOrder = {methodCall1, methodCall2};

        expect(context.getStatements().toArray()).toBe(expectedOrder);
    }

    @Test
    public void getStackedExpressionsShouldReturnEmptyCollectionIfNoExpressionsAreStacked() {
        expect(context.getStackedExpressions()).toBe(empty());
    }

    @Test
    public void getStackedExpressionsShouldReturnExpressionsOnStack() {
        final Expression expression = mock(Expression.class);

        context.push(expression);

        expect(context.getStackedExpressions().toArray()).toBe(new Object[]{expression});
    }

    @Test
    public void peekShouldFailIfStackIsEmpty() {
        expect(context::peek).toThrow(IllegalStateException.class);
    }

    @Test
    public void peekShouldReturnTopStackElementWithoutChangingTheStack() {
        final Expression stackedExpression = mock(Expression.class);

        context.push(stackedExpression);

        expect(context.peek()).toBe(stackedExpression);
        expect(context.getStackedExpressions().toArray()).toBe(new Object[]{stackedExpression});
    }
}
