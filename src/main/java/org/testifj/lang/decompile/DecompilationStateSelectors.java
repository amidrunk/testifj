package org.testifj.lang.decompile;

import org.testifj.lang.model.ElementType;

public final class DecompilationStateSelectors {

    private static final DecompilationStateSelector AT_LAST_ONE_STATEMENT = new DecompilationStateSelector() {
        @Override
        public boolean select(DecompilationContext context, int byteCode) {
            return !context.getStatements().isEmpty();
        }
    };

    public static DecompilationStateSelector atLeastOneStatement() {
        return AT_LAST_ONE_STATEMENT;
    }

    public static DecompilationStateSelector[] STACK_SIZE_IS_AT_LEAST = {
            stackSizeIsAtLeastUnCached(1),
            stackSizeIsAtLeastUnCached(2),
            stackSizeIsAtLeastUnCached(3)
    };

    public static DecompilationStateSelector stackSizeIsAtLeast(int count) {
        assert count > 0 : "Count must be positive";

        if (count <= STACK_SIZE_IS_AT_LEAST.length) {
            return STACK_SIZE_IS_AT_LEAST[count - 1];
        }

        return stackSizeIsAtLeastUnCached(count);
    }

    public static DecompilationStateSelector elementIsStacked(ElementType elementType) {
        assert elementType != null : "Element type can't be null";
        return (context,byteCode) -> !context.getStack().isEmpty() && context.getStack().peek().getElementType() == elementType;
    }

    private static DecompilationStateSelector stackSizeIsAtLeastUnCached(int count) {
        return new DecompilationStateSelector() {
            @Override
            public boolean select(DecompilationContext context, int byteCode) {
                return context.getStack().size() >= count;
            }
        };
    }


}
