package org.testifj.lang;

import java.io.IOException;

@FunctionalInterface
public interface DecompilerEnhancement {

    void enhance(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException;

}
