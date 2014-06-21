package org.testifj.lang.decompile;

import java.io.IOException;

// TODO: Should be called "ByteCodeProcessor" or something
// TODO: Should not return boolean
// TODO: Should be the same as enhancement
@FunctionalInterface
public interface DecompilerExtension {

    DecompilerExtension NOP = new DecompilerExtension() {
        @Override
        public void decompile(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
        }
    };

    void decompile(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException;

}
