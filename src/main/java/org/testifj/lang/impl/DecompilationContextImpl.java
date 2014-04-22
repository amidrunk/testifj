package org.testifj.lang.impl;

import org.testifj.lang.DecompilationContext;
import org.testifj.lang.Decompiler;
import org.testifj.lang.Method;
import org.testifj.lang.TypeResolver;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Statement;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class DecompilationContextImpl implements DecompilationContext {

    private final Decompiler decompiler;

    private final Method method;

    private final ProgramCounter programCounter;

    private final Stack<ExpressionWithPC> stack = new Stack<>();

    private final Set<StatementWithPC> statements = new TreeSet<>();

    private final TypeResolver typeResolver;

    private final AtomicInteger contextVersion = new AtomicInteger();

    public DecompilationContextImpl(Decompiler decompiler, Method method, ProgramCounter programCounter, TypeResolver typeResolver) {
        assert decompiler != null : "Decompiler can't be null";
        assert method != null : "Method can't be null";
        assert programCounter != null : "Program counter can't be null";
        assert typeResolver != null : "Type resolver can't be null";

        this.decompiler = decompiler;
        this.method = method;
        this.programCounter = programCounter;
        this.typeResolver = typeResolver;
    }

    @Override
    public Decompiler getDecompiler() {
        return decompiler;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Type resolveType(String internalName) {
        assert internalName != null && !internalName.isEmpty() : "Internal type name can't be null or empty";

        return typeResolver.resolveType(internalName.replace('/', '.'));
    }

    @Override
    public List<Expression> getStackedExpressions() {
        return Arrays.asList(stack.stream().map(ExpressionWithPC::expression).toArray(Expression[]::new));
    }

    @Override
    public boolean reduce() {
        if (stack.isEmpty()) {
            return false;
        }

        checkReducable(stack.peek());

        final ExpressionWithPC expressionWithPC = stack.pop();

        statements.add(new StatementWithPC((Statement) expressionWithPC.expression(), expressionWithPC.pc(), expressionWithPC.version()));

        return true;
    }

    @Override
    public boolean reduceAll() throws IllegalStateException {
        if (stack.isEmpty()) {
            return false;
        }

        stack.forEach(this::checkReducable);
        stack.forEach(e -> statements.add(new StatementWithPC((Statement) e.expression(), e.pc(), e.version())));
        stack.clear();

        return true;
    }

    @Override
    public void enlist(Statement statement) {
        assert statement != null : "Statement can't be null";

        reduceAll();

        statements.add(new StatementWithPC(statement, getProgramCounter().get(), contextVersion.incrementAndGet()));
    }

    @Override
    public void push(Expression expression) {
        assert expression != null : "Expression can't be null";

        stack.push(new ExpressionWithPC(expression, getProgramCounter().get(), contextVersion.incrementAndGet()));
    }

    @Override
    public Expression pop() {
        checkStackNotEmpty();
        return stack.pop().expression();
    }

    @Override
    public Expression peek() throws IllegalStateException {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }

        return stack.peek().expression();
    }

    @Override
    public List<Statement> getStatements() {
        final Statement[] statements = this.statements.stream()
                .map(StatementWithPC::statement)
                .toArray(Statement[]::new);

        return Arrays.asList(statements);
    }

    @Override
    public boolean hasStackedExpressions() {
        return !stack.isEmpty();
    }

    @Override
    public void replaceStatement(int index, Statement newStatement) {
        assert newStatement != null : "New statement can't be null";

        final ArrayList<StatementWithPC> statementListCopy = new ArrayList<>(statements);

        statementListCopy.set(index, new StatementWithPC(newStatement, programCounter.get(), contextVersion.incrementAndGet()));

        statements.clear();
        statements.addAll(statementListCopy);
    }

    @Override
    public void removeStatement(int index) {
        final ArrayList<StatementWithPC> copy = new ArrayList<>(statements);

        copy.remove(index);

        statements.clear();
        statements.addAll(copy);
    }

    @Override
    public ProgramCounter getProgramCounter() {
        return programCounter;
    }

    private void checkStackNotEmpty() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("No syntax element is available on the stack");
        }
    }

    private void checkReducable(ExpressionWithPC stackedExpression) {
        if (!(stackedExpression.expression() instanceof Statement)) {
            throw new IllegalStateException("Stacked expression is not a statement: " + stackedExpression.expression());
        }
    }

    private static final class ExpressionWithPC implements Comparable<ExpressionWithPC> {

        private final Expression expression;

        private final int pc;

        private final int contextVersion;

        private ExpressionWithPC(Expression expression, int pc, int contextVersion) {
            this.expression = expression;
            this.pc = pc;
            this.contextVersion = contextVersion;
        }

        public Expression expression() {
            return expression;
        }

        public int pc() {
            return pc;
        }

        public int version() {
            return contextVersion;
        }

        @Override
        public int compareTo(ExpressionWithPC o) {
            if (pc == o.pc) {
                return contextVersion - o.contextVersion;
            }

            return pc - o.pc;
        }

        @Override
        public String toString() {
            return "ExpressionWithPC{" +
                    "expression=" + expression +
                    ", pc=" + pc +
                    ", contextVersion=" + contextVersion +
                    '}';
        }
    }

    private final class StatementWithPC implements Comparable<StatementWithPC> {

        private final Statement statement;

        private final int pc;

        private final int contextVersion;

        private StatementWithPC(Statement statement, int pc, int contextVersion) {
            this.statement = statement;
            this.pc = pc;
            this.contextVersion = contextVersion;
        }

        public Statement statement() {
            return statement;
        }

        public int pc() {
            return pc;
        }

        @Override
        public int compareTo(StatementWithPC o) {
            if (pc == o.pc) {
                return contextVersion - o.contextVersion;
            }

            return pc - o.pc;
        }

        @Override
        public String toString() {
            return "StatementWithPC{" +
                    "statement=" + statement +
                    ", pc=" + pc +
                    ", contextVersion=" + contextVersion +
                    '}';
        }
    }
}