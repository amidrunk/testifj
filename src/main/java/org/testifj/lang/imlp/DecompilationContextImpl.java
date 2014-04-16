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
    public void reduce() {
        checkStackNotEmpty();

        final Expression stackedExpression = stack.peek();

        if (!(stackedExpression instanceof Statement)) {
            throw new IllegalStateException("Stacked expression is not an expression: " + stackedExpression);
        }

        enlist((Statement) stack.pop());
    }

    @Override
    public void enlist(Statement statement) {
        assert statement != null : "Statement can't be null";

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

    private void checkStackNotEmpty() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("No syntax element is available on the stack");
        }
    }
}
