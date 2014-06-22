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

    public static DecompilationStateSelector elementIsStacked(ElementType elementType) {
        assert elementType != null : "Element type can't be null";
        return (context,byteCode) -> !context.getStack().isEmpty() && context.getStack().peek().getElementType() == elementType;
    }



}
