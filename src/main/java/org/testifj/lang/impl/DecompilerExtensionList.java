package org.testifj.lang.impl;

import org.testifj.lang.CodeStream;
import org.testifj.lang.DecompilationContext;
import org.testifj.lang.DecompilerExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class DecompilerExtensionList implements DecompilerExtension {

    private final List<DecompilerExtension> decompilerExtensions;

    public DecompilerExtensionList() {
        this(Collections.emptyList());
    }

    private DecompilerExtensionList(List<DecompilerExtension> decompilerExtensions) {
        this.decompilerExtensions = Collections.unmodifiableList(decompilerExtensions);
    }

    private DecompilerExtensionList(List<DecompilerExtension> decompilerExtensions, DecompilerExtension additionalExtension) {
        final List<DecompilerExtension> extensions = new LinkedList<>(decompilerExtensions);

        extensions.add(additionalExtension);

        this.decompilerExtensions = Collections.unmodifiableList(extensions);
    }

    @Override
    public boolean decompile(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
        for (DecompilerExtension extension : decompilerExtensions) {
            if (extension.decompile(context, codeStream, byteCode)) {
                return true;
            }
        }

        return false;
    }

    public List<DecompilerExtension> extensions() {
        return decompilerExtensions;
    }

    public DecompilerExtensionList extend(DecompilerExtension extension) {
        assert extension != null : "Extension can't be null";
        return new DecompilerExtensionList(decompilerExtensions, extension);
    }

}
