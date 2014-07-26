package org.testifj.lang.codegeneration;

import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.model.Element;

import java.io.PrintWriter;

@FunctionalInterface
public interface CodeGeneratorDelegate<E extends Element> {

    void apply(CodeGenerationContext context, CodePointer<E> codePointer, PrintWriter out);

}
