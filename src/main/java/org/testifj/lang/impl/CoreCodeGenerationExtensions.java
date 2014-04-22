package org.testifj.lang.impl;

import org.testifj.lang.CodeGeneratorConfiguration;
import org.testifj.lang.CodeGeneratorExtension;
import org.testifj.lang.model.ElementType;

public final class CoreCodeGenerationExtensions {

    public static void configure(CodeGeneratorConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";
        configurationBuilder.extend(ElementType.RETURN, ret());
    }

    public static CodeGeneratorExtension ret() {
        return (context, codePointer, out) -> {
            out.append("return");
            return true;
        };
    }

}
