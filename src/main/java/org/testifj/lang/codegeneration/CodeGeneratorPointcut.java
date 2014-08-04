package org.testifj.lang.codegeneration;

import org.testifj.lang.decompile.CodePointer;

public interface CodeGeneratorPointcut {

    void proceed(CodeGenerationContext context, CodePointer codePointer);

}
