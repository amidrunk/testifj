package org.testifj.lang.impl;

import org.testifj.lang.DecompilationContext;
import org.testifj.lang.DecompilationProgressCallback;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Statement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class DecompilationHistoryCallback implements DecompilationProgressCallback {

    private final List<DecompilerState> decompilerStates = new LinkedList<>();

    @Override
    public void onDecompilationProgressed(DecompilationContext context) {
        decompilerStates.add(new DecompilerState(new ArrayList<>(context.getStackedExpressions()), new ArrayList<>(context.getStatements())));
    }

    public DecompilerState[] getDecompilerStates() {
        return decompilerStates.toArray(new DecompilerState[decompilerStates.size()]);
    }

    public static final class DecompilerState {

        private final List<Expression> stack;

        private final List<Statement> statements;

        public DecompilerState(List<Expression> stack, List<Statement> statements) {
            this.stack = stack;
            this.statements = statements;
        }

        public List<Expression> getStack() {
            return stack;
        }

        public List<Statement> getStatements() {
            return statements;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DecompilerState that = (DecompilerState) o;

            if (!stack.equals(that.stack)) return false;
            if (!statements.equals(that.statements)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = stack.hashCode();
            result = 31 * result + statements.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "DecompilerState{" +
                    "stack=" + stack +
                    ", statements=" + statements +
                    '}';
        }
    }

}
