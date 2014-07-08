package org.testifj.lang.decompile.impl;

import org.testifj.lang.TypeResolver;
import org.testifj.lang.Types;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.Decompiler;
import org.testifj.lang.decompile.LineNumberCounter;
import org.testifj.lang.model.*;

import java.lang.reflect.Type;
import java.util.*;

import org.testifj.util.SingleThreadedStack;
import org.testifj.util.Stack;
import org.testifj.util.TransformedStack;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class DecompilationContextImpl implements DecompilationContext {

    private final Decompiler decompiler;

    private final Method method;

    private final ProgramCounter programCounter;

    private final LineNumberCounter lineNumberCounter;

    private final SingleThreadedStack<ExpressionWithPC> stack = new SingleThreadedStack<>();

    private final Sequence<StatementWithPC> statements = new LinkedSequence<>();

    private final TypeResolver typeResolver;

    private final AtomicInteger contextVersion = new AtomicInteger();

    private final AtomicBoolean aborted = new AtomicBoolean(false);

    private final Stack<Expression> visibleStack = new TransformedStack<>(stack, expression -> {
        configureContextMetaData(expression);
        return new ExpressionWithPC(expression, getProgramCounter().get(), contextVersion.incrementAndGet());
    }, ExpressionWithPC::expression);

    private final Sequence<Statement> visibleStatements;

    private final int startPC;

    public DecompilationContextImpl(Decompiler decompiler,
                                    Method method,
                                    ProgramCounter programCounter,
                                    LineNumberCounter lineNumberCounter,
                                    TypeResolver typeResolver,
                                    int startPC) {
        assert decompiler != null : "Decompiler can't be null";
        assert method != null : "Method can't be null";
        assert programCounter != null : "Program counter can't be null";
        assert lineNumberCounter != null : "Line number counter can't be null";
        assert typeResolver != null : "Type resolver can't be null";

        this.decompiler = decompiler;
        this.method = method;
        this.programCounter = programCounter;
        this.lineNumberCounter = lineNumberCounter;
        this.typeResolver = typeResolver;
        this.visibleStatements = new TransformedSequence<>(statements, StatementWithPC::statement, statement -> {
            final StatementWithPC statementWithPC = new StatementWithPC(statement, programCounter.get(), contextVersion.incrementAndGet());
            configureContextMetaData(statement);
            return statementWithPC;
        });
        this.startPC = startPC;
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
    public int getStackSize() {
        return stack.size();
    }

    public boolean isStackCompliantWithComputationalCategories(int... computationalCategories) {
        assert computationalCategories != null : "Computational categories can't be null";

        if (computationalCategories.length > stack.size()) {
            return false;
        }

        final Iterable<ExpressionWithPC> subStack = (stack.size() == computationalCategories.length
                ? stack
                : stack.tail(stack.size() - computationalCategories.length));

        int index = 0;

        for (ExpressionWithPC expressionWithPC : subStack) {
            final int actualComputationalCategory = Types.getComputationalCategory(expressionWithPC.expression.getType());

            if (computationalCategories[index++] != actualComputationalCategory) {
                return false;
            }
        }

        return true;
    }

    public Stack<Expression> getStack() {
        return visibleStack;
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
        final StatementWithPC newStatement = new StatementWithPC((Statement) expressionWithPC.expression(), expressionWithPC.pc(), expressionWithPC.version());
        final Sequence.SingleElement<StatementWithPC> selector = statements.first(s -> s.pc() > expressionWithPC.pc());

        if (selector.exists()) {
            selector.insertBefore(newStatement);
        } else {
            statements.add(newStatement);
        }

        return true;
    }

    @Override
    public boolean reduceAll() throws IllegalStateException {
        if (stack.isEmpty()) {
            return false;
        }

        /*stack.forEach(this::checkReducable);
        stack.forEach(e -> statements.add(new StatementWithPC((Statement) e.expression(), e.pc(), e.version())));
        stack.clear();*/

        while (!stack.isEmpty()) {
            reduce();
        }

        return true;
    }

    @Override
    public void enlist(Statement statement) {
        assert statement != null : "Statement can't be null";

        statements.add(new StatementWithPC(statement, getProgramCounter().get(), contextVersion.incrementAndGet()));

        configureContextMetaData(statement);
    }

    @Override
    public void push(Expression expression) {
        assert expression != null : "Expression can't be null";

        stack.push(new ExpressionWithPC(expression, getProgramCounter().get(), contextVersion.incrementAndGet()));

        configureContextMetaData(expression);
    }

    @Override
    public void insert(int offset, Expression expression) {
        stack.insert(stack.size() + offset, new ExpressionWithPC(expression, programCounter.get(), contextVersion.incrementAndGet()));
        configureContextMetaData(expression);
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
    public Sequence<Statement> getStatements() {
        return visibleStatements;
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

        configureContextMetaData(newStatement);
    }

    @Override
    public void removeStatement(int index) {
        final ArrayList<StatementWithPC> copy = new ArrayList<>(statements);

        copy.remove(index);

        statements.clear();
        statements.addAll(copy);
    }

    @Override
    public void abort() {
        this.aborted.set(true);
    }

    @Override
    public boolean isAborted() {
        return this.aborted.get();
    }

    @Override
    public int getStartPC() {
        return startPC;
    }

    @Override
    public ProgramCounter getProgramCounter() {
        return programCounter;
    }

    public LineNumberCounter getLineNumberCounter() {
        return lineNumberCounter;
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

    private void configureContextMetaData(Element element) {
        final ElementMetaData metaData = element.getMetaData();

        if (metaData != null) {
            final int lineNumber = lineNumberCounter.get();
            final int programCounter = getProgramCounter().get();

            metaData.setAttribute(ElementMetaData.LINE_NUMBER, lineNumber);
            metaData.setAttribute(ElementMetaData.PROGRAM_COUNTER, programCounter);
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
