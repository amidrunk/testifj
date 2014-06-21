package org.testifj.lang.decompile;

public interface DecompilationProgressCallback {

    DecompilationProgressCallback NULL = context -> {};

    void onDecompilationProgressed(DecompilationContext context);

}
