package org.testifj.lang;

public interface CodeGenerationContext {

    int getIndentationLevel();

    CodeGenerationContext subSection();

    /**
     * Delegates generation of the provided code pointer to a handler valid within this context. This
     * would typically be dispatched back to the original code generator.
     *
     * @param codePointer The code pointer that should be generated.
     */
    void delegate(CodePointer codePointer);

}
