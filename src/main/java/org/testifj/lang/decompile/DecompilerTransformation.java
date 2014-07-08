package org.testifj.lang.decompile;

import java.io.IOException;

public interface DecompilerTransformation<R> {

    void apply(DecompilationContext context, CodeStream codeStream, int byteCode, R result) throws IOException;

}
