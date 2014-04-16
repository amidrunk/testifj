package org.testifj.lang;

import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Statement;

import java.util.List;

public interface DecompilationContext {

    /**
     * Reduces the stack, i.e. pops the stack and puts the stacked statement on in the
     * statement list. The element on the stack must be a valid statement.
     *
     * @throws java.lang.IllegalStateException Thrown if there's no expression on the stack
     *                                         or the stacked expression is not a valid statement.
     */
    void reduce() throws IllegalStateException;

    /**
     * Called by the decompiler when a statements has been reduced and needs to be enlisted.
     * This will add the statement to the statement list.
     *
     * @param statement The statement that should be enlisted.
     */
    void enlist(Statement statement);

    /**
     * Pushes an expression onto the stack.
     *
     * @param expression The expression that should be pushed onto the stack.
     */
    void push(Expression expression);

    /**
     * Pops an expression from the stack. If there's no expression available no the stack,
     * an <code>IllegalStateException</code> will be thrown.
     *
     * @return The popped statement.
     * @throws java.lang.IllegalStateException Thrown if there's no expression on the stack.
     */
    Expression pop() throws IllegalStateException;

    /**
     * The statements that have been enlisted thus far in the context.
     *
     * @return The enlisted statements.
     */
    List<Statement> getStatements();

}
