package org.testifj.lang.classfile.impl;

import org.testifj.lang.codegeneration.CodeGenerationContext;
import org.testifj.lang.codegeneration.CodeGeneratorConfiguration;
import org.testifj.lang.codegeneration.CodeGeneratorExtension;
import org.testifj.lang.codegeneration.ElementSelector;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;

import java.util.Optional;

public final class SimpleCodeGeneratorConfiguration implements CodeGeneratorConfiguration {

    private CodeGeneratorExtensionCandidate[] extensionCandidates;

    private SimpleCodeGeneratorConfiguration(CodeGeneratorExtensionCandidate[] extensionCandidates) {
        this.extensionCandidates = extensionCandidates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CodeGeneratorExtension<? extends Element> getExtension(CodeGenerationContext context, CodePointer<? extends Element> codePointer) {
        assert codePointer != null : "Code pointer can't be null";

        CodeGeneratorExtensionCandidate candidate = extensionCandidates[codePointer.getElement().getElementType().ordinal()];

        while (candidate != null) {
            if (candidate.selector().matches((CodePointer) codePointer)) {
                return candidate.extension();
            }

            candidate = candidate.next().orElseGet(() -> null);
        }

        return null;
    }

    public static final class Builder implements CodeGeneratorConfiguration.Builder {

        private final CodeGeneratorExtensionCandidate[] extensionCandidates = new CodeGeneratorExtensionCandidate[ElementType.values().length];

        @Override
        public <E extends Element> CodeGeneratorConfiguration.Builder extend(ElementSelector<E> elementSelector,
                                                                             CodeGeneratorExtension<E> extension) {
            assert elementSelector != null : "Element type can't be null";
            assert extension != null : "Extension can't be null";

            final ElementType elementType = elementSelector.getElementType();
            final CodeGeneratorExtensionCandidate newCandidate = new CodeGeneratorExtensionCandidate(
                    elementSelector,
                    extension,
                    Optional.<CodeGeneratorExtensionCandidate>empty());

            CodeGeneratorExtensionCandidate existingCandidate = extensionCandidates[elementType.ordinal()];

            if (existingCandidate == null) {
                extensionCandidates[elementType.ordinal()] = newCandidate;
            } else {
                while (existingCandidate.next().isPresent()) {
                    existingCandidate = existingCandidate.next().get();
                }

                existingCandidate.next(newCandidate);
            }

            return this;
        }

        @Override
        public CodeGeneratorConfiguration build() {
            return new SimpleCodeGeneratorConfiguration(extensionCandidates);
        }
    }

    private static final class CodeGeneratorExtensionCandidate {

        private final ElementSelector<? extends Element> elementElementSelector;

        private final CodeGeneratorExtension<? extends Element> codeGeneratorExtension;

        private Optional<CodeGeneratorExtensionCandidate> nextCandidate;

        private CodeGeneratorExtensionCandidate(ElementSelector<? extends Element> elementElementSelector,
                                                CodeGeneratorExtension<? extends Element> codeGeneratorExtension,
                                                Optional<CodeGeneratorExtensionCandidate> nextCandidate) {
            this.elementElementSelector = elementElementSelector;
            this.codeGeneratorExtension = codeGeneratorExtension;
            this.nextCandidate = nextCandidate;
        }

        public ElementSelector<? extends Element> selector(){
            return elementElementSelector;
        }

        public CodeGeneratorExtension<? extends Element> extension() {
            return codeGeneratorExtension;
        }

        public Optional<CodeGeneratorExtensionCandidate> next() {
            return nextCandidate;
        }

        public void next(CodeGeneratorExtensionCandidate candidate) {
            this.nextCandidate = Optional.of(candidate);
        }

    }

}
