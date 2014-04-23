package org.testifj.lang.impl;

import org.testifj.lang.CodeGenerationContext;
import org.testifj.lang.CodeGenerationDelegate;
import org.testifj.lang.CodePointer;
import org.testifj.lang.CodeStyle;

import java.lang.reflect.Type;

public final class CodeGenerationContextImpl implements CodeGenerationContext {

    private final CodeGenerationDelegate codeGenerationDelegate;

    private final CodeStyle codeStyle;

    private final int indentationLevel;

    public CodeGenerationContextImpl(CodeGenerationDelegate codeGenerationDelegate, CodeStyle codeStyle) {
        this(codeGenerationDelegate, codeStyle, 0);
    }

    private CodeGenerationContextImpl(CodeGenerationDelegate codeGenerationDelegate, CodeStyle codeStyle, int indentationLevel) {
        assert codeGenerationDelegate != null : "Code generation delegate can't be null";

        this.indentationLevel = indentationLevel;
        this.codeStyle = codeStyle;
        this.codeGenerationDelegate = codeGenerationDelegate;
    }

    @Override
    public int getIndentationLevel() {
        return indentationLevel;
    }

    @Override
    public CodeGenerationContext subSection() {
        return new CodeGenerationContextImpl(codeGenerationDelegate, codeStyle, indentationLevel + 1);
    }

    @Override
    public void delegate(CodePointer codePointer) {
        assert codePointer != null : "Code pointer can't be null";

        codeGenerationDelegate.delegate(this, codePointer);
    }

    @Override
    public CodeStyle getCodeStyle() {
        return codeStyle;
    }
}
