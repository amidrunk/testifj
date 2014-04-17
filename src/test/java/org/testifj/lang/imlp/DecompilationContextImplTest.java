package org.testifj.lang.imlp;

import org.junit.Test;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;
import org.testifj.lang.model.Statement;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.empty;

public class DecompilationContextImplTest {

    private final DecompilationContextImpl context = new DecompilationContextImpl();

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
    public void enlistShouldReduceTheStackCompletely() {
        final MethodCall methodCall1 = mock(MethodCall.class);
        final MethodCall methodCall2 = mock(MethodCall.class);

        context.push(methodCall1);
        context.enlist(methodCall2);

        expect(context.getStatements().toArray()).toBe(new Object[]{
                methodCall1, methodCall2
        });
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

}
