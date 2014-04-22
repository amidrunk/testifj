package org.testifj.lang;

import org.testifj.CodeGenerationContext;
import org.testifj.CodePointer;

import java.io.PrintWriter;

import static java.util.Arrays.stream;

public final class CodeGeneratorExtensionList implements CodeGeneratorExtension {

    private final CodeGeneratorExtensionLink firstLink;

    public CodeGeneratorExtensionList(CodeGeneratorExtension head, CodeGeneratorExtension ... tail) {
        assert head != null : "Head extension can't be null";

        firstLink = new CodeGeneratorExtensionLink(head, link(tail, 0));
    }

    @Override
    public boolean generateCode(CodeGenerationContext context, CodePointer codePointer, PrintWriter out) {
        CodeGeneratorExtensionLink currentLink = firstLink;

        while (currentLink != null) {
            if (currentLink.extension().generateCode(context, codePointer, out)) {
                return true;
            }

            currentLink = currentLink.next();
        }

        return false;
    }

    private CodeGeneratorExtensionLink link(CodeGeneratorExtension[] extensions, int index) {
        if (index >= extensions.length) {
            return null;
        }

        final CodeGeneratorExtension extension = extensions[index];

        assert extension != null : "No extension can be null";

        return new CodeGeneratorExtensionLink(extension, link(extensions, index + 1));
    }

    private final class CodeGeneratorExtensionLink {

        private final CodeGeneratorExtension extension;

        private final CodeGeneratorExtensionLink next;

        private CodeGeneratorExtensionLink(CodeGeneratorExtension extension, CodeGeneratorExtensionLink next) {
            this.extension = extension;
            this.next = next;
        }

        public CodeGeneratorExtension extension() {
            return extension;
        }

        public CodeGeneratorExtensionLink next() {
            return next;
        }
    }

}
