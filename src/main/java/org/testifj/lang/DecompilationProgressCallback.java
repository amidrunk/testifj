package org.testifj.lang;

public interface DecompilationProgressCallback {

    DecompilationProgressCallback NULL = context -> {};

    void onDecompilationProgressed(DecompilationContext context);

}
