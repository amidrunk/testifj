package org.testifj.lang.impl;

import org.testifj.lang.CodeGenerationContext;
import org.testifj.lang.CodeGenerationDelegate;
import org.testifj.lang.CodePointer;
import org.testifj.lang.CodeStyle;

import java.lang.reflect.Type;

public final class CodeGenerationContextImpl implements CodeGenerationContext {

    private final int indentationLevel;

    private final CodeGenerationDelegate codeGenerationDelegate;

    public CodeGenerationContextImpl(CodeGenerationDelegate codeGenerationDelegate) {
        this(codeGenerationDelegate, 0);
    }

    private CodeGenerationContextImpl(CodeGenerationDelegate codeGenerationDelegate, int indentationLevel) {
        assert codeGenerationDelegate != null : "Code generation delegate can't be null";

        this.indentationLevel = indentationLevel;
        this.codeGenerationDelegate = codeGenerationDelegate;
    }

    @Override
    public int getIndentationLevel() {
        return indentationLevel;
    }

    @Override
    public CodeGenerationContext subSection() {
        return new CodeGenerationContextImpl(codeGenerationDelegate, indentationLevel + 1);
    }

    @Override
    public void delegate(CodePointer codePointer) {
        assert codePointer != null : "Code pointer can't be null";

        codeGenerationDelegate.delegate(this, codePointer);
    }

    @Override
    public CodeStyle getCodeStyle() {
        return new CodeStyle() {
            @Override
            public String getTypeName(Type type) {
                return ((Class) type).getSimpleName();
            }
        };
    }
}
