package org.testifj.lang;

import java.io.PrintWriter;

@FunctionalInterface
public interface CodeGeneratorExtension {

    boolean generateCode(CodeGenerationContext context, CodePointer codePointer, PrintWriter out);

}
