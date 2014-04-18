package org.testifj.lang;

import java.io.IOException;

@FunctionalInterface
public interface DecompilerExtension {

    boolean decompile(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException;

}
