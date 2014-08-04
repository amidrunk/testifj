package org.testifj.lang.codegeneration;

import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.Element;

public interface CodeGeneratorConfiguration {

    CodeGeneratorDelegate<? extends Element> getDelegate(CodeGenerationContext context, CodePointer<? extends Element> codePointer);

    CodeGeneratorAdvice<? extends Element>[] getAdvices(CodeGenerationContext context, CodePointer<? extends Element> codePointer);

}
