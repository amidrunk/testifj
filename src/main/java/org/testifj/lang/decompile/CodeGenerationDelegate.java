package org.testifj.lang.decompile;

@FunctionalInterface
public interface CodeGenerationDelegate {

    void delegate(CodeGenerationContext context, CodePointer codePointer);

}
