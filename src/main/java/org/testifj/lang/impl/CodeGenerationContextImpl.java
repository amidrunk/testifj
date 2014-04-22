package org.testifj.lang.impl;

import org.testifj.lang.CodeGenerationContext;

public final class CodeGenerationContextImpl implements CodeGenerationContext {

    private final int indentationLevel;

    public CodeGenerationContextImpl() {
        this(0);
    }

    private CodeGenerationContextImpl(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }

    @Override
    public int getIndentationLevel() {
        return indentationLevel;
    }

    @Override
    public CodeGenerationContext subSection() {
        return new CodeGenerationContextImpl(indentationLevel + 1);
    }
}
