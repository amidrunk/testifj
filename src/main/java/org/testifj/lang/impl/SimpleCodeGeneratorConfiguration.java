package org.testifj.lang.impl;

import org.testifj.lang.*;
import org.testifj.lang.model.ElementType;

public final class SimpleCodeGeneratorConfiguration implements CodeGeneratorConfiguration {

    private CodeGeneratorExtension[] extensions;

    private SimpleCodeGeneratorConfiguration(CodeGeneratorExtension[] extensions) {
        this.extensions = extensions;
    }

    @Override
    public CodeGeneratorExtension getExtension(CodeGenerationContext context, CodePointer codePointer) {
        assert codePointer != null : "Code pointer can't be null";

        return extensions[codePointer.getElement().getElementType().ordinal()];
    }

    public static final class Builder implements CodeGeneratorConfiguration.Builder {

        private final CodeGeneratorExtension[] extensions = new CodeGeneratorExtension[ElementType.values().length];

        @Override
        public CodeGeneratorConfiguration.Builder extend(ElementType elementType, CodeGeneratorExtension extension) {
            assert elementType != null : "Element type can't be null";
            assert extension != null : "Extension can't be null";

            final CodeGeneratorExtension configuredExtension = extensions[elementType.ordinal()];

            if (configuredExtension == null) {
                extensions[elementType.ordinal()] = extension;
            } else {
                extensions[elementType.ordinal()] = new CodeGeneratorExtensionList(configuredExtension, extension);
            }

            return this;
        }

        @Override
        public CodeGeneratorConfiguration build() {
            return new SimpleCodeGeneratorConfiguration(extensions);
        }
    }

}
