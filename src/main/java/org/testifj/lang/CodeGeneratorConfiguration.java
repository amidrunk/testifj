package org.testifj.lang;

import org.testifj.CodeGenerationContext;
import org.testifj.CodePointer;
import org.testifj.lang.model.ElementType;

public interface CodeGeneratorConfiguration {

    CodeGeneratorExtension getExtension(CodeGenerationContext context, CodePointer codePointer);

    public interface Builder {

        Builder extend(ElementType elementType, CodeGeneratorExtension extension);

        CodeGeneratorConfiguration build();

    }
}
