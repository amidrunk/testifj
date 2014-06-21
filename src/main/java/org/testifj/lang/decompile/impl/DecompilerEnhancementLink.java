package org.testifj.lang.decompile.impl;

import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerEnhancement;

import java.io.IOException;

public final class DecompilerEnhancementLink implements DecompilerEnhancement {


    private final DecompilerEnhancement firstEnhancement;

    private final DecompilerEnhancement lastEnhancement;

    public DecompilerEnhancementLink(DecompilerEnhancement firstEnhancement, DecompilerEnhancement lastEnhancement) {
        assert firstEnhancement != null : "First enhancement can't be null";
        assert lastEnhancement != null : "Last enhancement can't be null";

        this.firstEnhancement = firstEnhancement;
        this.lastEnhancement = lastEnhancement;
    }

    @Override
    public void enhance(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
        firstEnhancement.enhance(context, codeStream, byteCode);
        lastEnhancement.enhance(context, codeStream, byteCode);
    }
}
