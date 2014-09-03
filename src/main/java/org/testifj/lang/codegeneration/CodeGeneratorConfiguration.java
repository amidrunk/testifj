package org.testifj.lang.codegeneration;

import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.Element;

import java.util.Iterator;
import java.util.List;

public interface CodeGeneratorConfiguration {

    CodeGeneratorDelegate<? extends Element> getDelegate(CodeGenerationContext context, CodePointer<? extends Element> codePointer);

    Iterator<CodeGeneratorAdvice<? extends Element>> getAdvices(CodeGenerationContext context, CodePointer<? extends Element> codePointer);

}
