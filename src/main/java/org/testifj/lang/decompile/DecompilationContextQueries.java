package org.testifj.lang.decompile;

import org.testifj.lang.model.Expression;
import org.testifj.lang.model.ModelQuery;
import org.testifj.lang.model.Statement;

import java.util.List;
import java.util.Optional;

public final class DecompilationContextQueries {

    private static final ModelQuery<DecompilationContext, Statement> LAST_DECOMPILED_STATEMENT = new ModelQuery<DecompilationContext, Statement>() {
        @Override
        public Optional<Statement> from(DecompilationContext context) {
            if (context == null) {
                return Optional.empty();
            }

            return context.getStatements().last().optional();
        }
    };

    private static final ModelQuery<DecompilationContext, Expression> PREVIOUS_VALUE = new ModelQuery<DecompilationContext, Expression>() {
        @Override
        public Optional<Expression> from(DecompilationContext context) {
            if (context == null) {
                return Optional.empty();
            }

            final List<Expression> stack = context.getStackedExpressions();

            if (stack.size() < 2) {
                return Optional.empty();
            }

            return Optional.of(stack.get(stack.size() - 2));
        }
    };

    private static final ModelQuery<DecompilationContext, Expression> CURRENT_VALUE = new ModelQuery<DecompilationContext, Expression>() {
        @Override
        public Optional<Expression> from(DecompilationContext from) {
            return from == null || !from.hasStackedExpressions()
                    ? Optional.<Expression>empty()
                    : Optional.of(from.peek());
        }
    };

    public static ModelQuery<DecompilationContext, Statement> lastDecompiledStatement() {
        return LAST_DECOMPILED_STATEMENT;
    }

    public static ModelQuery<DecompilationContext, Expression> previousValue() {
        return PREVIOUS_VALUE;
    }

    public static ModelQuery<DecompilationContext, Expression> currentValue() {
        return CURRENT_VALUE;
    }

}
