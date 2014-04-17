package org.testifj;

public final class CodeGenerationContext {

    private final int indentationLevel;

    public CodeGenerationContext() {
        this(0);
    }

    private CodeGenerationContext(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }

    public int getIndentationLevel() {
        return indentationLevel;
    }

    public CodeGenerationContext subSection() {
        return new CodeGenerationContext(indentationLevel + 1);
    }
}
