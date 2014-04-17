package org.testifj.lang.imlp;

import org.testifj.lang.DecompilationContext;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Statement;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public final class DecompilationContextImpl implements DecompilationContext {

    private final Stack<Expression> stack = new Stack<>();

    private final List<Statement> statements = new LinkedList<>();

    @Override
    public boolean reduce() {
        if (stack.isEmpty()) {
            return false;
        }

        checkReducable(stack.peek());

        statements.add((Statement) stack.pop());

        return true;
    }

    @Override
    public boolean reduceAll() throws IllegalStateException {
        if (stack.isEmpty()) {
            return false;
        }

        stack.forEach(this::checkReducable);
        stack.forEach(e -> statements.add((Statement) e));
        stack.clear();

        return true;
    }

    @Override
    public void enlist(Statement statement) {
        assert statement != null : "Statement can't be null";

        reduceAll();

        statements.add(statement);
    }

    @Override
    public void push(Expression expression) {
        assert expression != null : "Expression can't be null";

        stack.push(expression);
    }

    @Override
    public Expression pop() {
        checkStackNotEmpty();
        return stack.pop();
    }

    @Override
    public List<Statement> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public boolean hasStackedExpressions() {
        return !stack.isEmpty();
    }

    @Override
    public void replaceStatement(int index, Statement newStatement) {
        assert newStatement != null : "New statement can't be null";

        statements.set(index, newStatement);
    }

    private void checkStackNotEmpty() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("No syntax element is available on the stack");
        }
    }

    private void checkReducable(Expression stackedExpression) {
        if (!(stackedExpression instanceof Statement)) {
            throw new IllegalStateException("Stacked expression is not an expression: " + stackedExpression);
        }
    }
}
