package org.testifj.lang;

public interface CodeGenerationContext {

    int getIndentationLevel();

    CodeGenerationContext subSection();

}
