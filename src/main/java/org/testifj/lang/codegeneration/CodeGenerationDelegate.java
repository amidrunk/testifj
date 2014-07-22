package org.testifj.lang.codegeneration;

import org.testifj.lang.decompile.CodePointer;

@FunctionalInterface
public interface CodeGenerationDelegate {

    void delegate(CodeGenerationContext context, CodePointer codePointer);

}
