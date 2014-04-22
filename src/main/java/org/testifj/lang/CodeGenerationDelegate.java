package org.testifj.lang;

@FunctionalInterface
public interface CodeGenerationDelegate {

    void delegate(CodeGenerationContext context, CodePointer codePointer);

}
