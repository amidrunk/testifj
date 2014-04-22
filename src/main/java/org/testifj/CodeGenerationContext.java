package org.testifj;

public interface CodeGenerationContext {
    int getIndentationLevel();

    CodeGenerationContext subSection();
}
