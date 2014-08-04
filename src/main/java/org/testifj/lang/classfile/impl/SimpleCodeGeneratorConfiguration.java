package org.testifj.lang.classfile.impl;

import org.testifj.lang.codegeneration.*;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;

import java.util.Optional;

public final class SimpleCodeGeneratorConfiguration implements CodeGeneratorConfiguration {

    private CodeGeneratorExtensionCandidate[] extensionCandidates;

    private CodeGeneratorAdvice[] advices;

    private SimpleCodeGeneratorConfiguration(CodeGeneratorExtensionCandidate[] extensionCandidates, CodeGeneratorAdvice[] advices) {
        this.extensionCandidates = extensionCandidates;
        this.advices = advices;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CodeGeneratorDelegate<? extends Element> getDelegate(CodeGenerationContext context, CodePointer<? extends Element> codePointer) {
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

    @Override
    public CodeGeneratorAdvice<? extends Element> getAdvice(CodeGenerationContext context, CodePointer<? extends Element> codePointer) {
        assert context != null : "Context can't be null";
        assert codePointer != null : "Code pointer can't be null";

        return null;
    }

    public static CodeGeneratorConfigurer configurer() {
        return new Configurer();
    }

    private static final class Configurer implements CodeGeneratorConfigurer {

        private final CodeGeneratorExtensionCandidate[] extensionCandidates = new CodeGeneratorExtensionCandidate[ElementType.values().length];

        private final CodeGeneratorAdvice[][] advices = new CodeGeneratorAdvice[ElementType.values().length][];

        @Override
        public <E extends Element> OnContinuation<E> on(ElementSelector<E> elementSelector) {
            return delegate -> {
                assert elementSelector != null : "Element type can't be null";
                assert delegate != null : "Extension can't be null";

                final ElementType elementType = elementSelector.getElementType();
                final CodeGeneratorExtensionCandidate newCandidate = new CodeGeneratorExtensionCandidate(
                        elementSelector,
                        delegate,
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
            };
        }

        @Override
        public <E extends Element> AroundContinuation<E> around(ElementSelector<E> elementSelector) {
            final int index = elementSelector.getElementType().ordinal();

            return null;
        }

        @Override
        public CodeGeneratorConfiguration configuration() {
            return new SimpleCodeGeneratorConfiguration(extensionCandidates, null);
        }
    }

    private static final class CodeGeneratorExtensionCandidate {

        private final ElementSelector<? extends Element> elementElementSelector;

        private final CodeGeneratorDelegate<? extends Element> codeGeneratorDelegate;

        private Optional<CodeGeneratorExtensionCandidate> nextCandidate;

        private CodeGeneratorExtensionCandidate(ElementSelector<? extends Element> elementElementSelector,
                                                CodeGeneratorDelegate<? extends Element> codeGeneratorDelegate,
                                                Optional<CodeGeneratorExtensionCandidate> nextCandidate) {
            this.elementElementSelector = elementElementSelector;
            this.codeGeneratorDelegate = codeGeneratorDelegate;
            this.nextCandidate = nextCandidate;
        }

        public ElementSelector<? extends Element> selector() {
            return elementElementSelector;
        }

        public CodeGeneratorDelegate<? extends Element> extension() {
            return codeGeneratorDelegate;
        }

        public Optional<CodeGeneratorExtensionCandidate> next() {
            return nextCandidate;
        }

        public void next(CodeGeneratorExtensionCandidate candidate) {
            this.nextCandidate = Optional.of(candidate);
        }

    }

}
