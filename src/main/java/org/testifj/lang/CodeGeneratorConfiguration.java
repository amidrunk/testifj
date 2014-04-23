package org.testifj.lang;

import org.testifj.lang.model.Element;

public interface CodeGeneratorConfiguration {

    CodeGeneratorExtension<? extends Element> getExtension(CodeGenerationContext context, CodePointer<? extends Element> codePointer);

    public interface Builder {

        <E extends Element> Builder extend(ElementSelector<E> elementSelector, CodeGeneratorExtension<E> extension);

        CodeGeneratorConfiguration build();

    }

}
