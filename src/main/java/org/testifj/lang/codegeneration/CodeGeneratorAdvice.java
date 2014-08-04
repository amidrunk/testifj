package org.testifj.lang.codegeneration;

import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.model.Element;

@FunctionalInterface
public interface CodeGeneratorAdvice<E extends Element> {

    void apply(CodeGenerationContext context, CodePointer<E> codePointer, CodeGeneratorPointcut pointcut);

}
