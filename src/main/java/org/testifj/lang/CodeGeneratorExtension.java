package org.testifj.lang;

import org.testifj.CodeGenerationContext;
import org.testifj.CodePointer;

import java.io.PrintWriter;

@FunctionalInterface
public interface CodeGeneratorExtension {

    boolean generateCode(CodeGenerationContext context, CodePointer codePointer, PrintWriter out);

}
