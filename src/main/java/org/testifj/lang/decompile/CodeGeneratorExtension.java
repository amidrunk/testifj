package org.testifj.lang.decompile;

import org.testifj.lang.model.Element;

import java.io.PrintWriter;

@FunctionalInterface
public interface CodeGeneratorExtension<E extends Element> {

    void call(CodeGenerationContext context, CodePointer<E> codePointer, PrintWriter out);

}
