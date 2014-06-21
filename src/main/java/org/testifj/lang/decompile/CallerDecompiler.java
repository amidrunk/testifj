package org.testifj.lang.decompile;

import org.testifj.Caller;

import java.io.IOException;

/**
 * A <code>CallerDecompiler</code> decompiles a specific line in a class file and returns an
 * abstract syntax tree of the calling code.
 */
public interface CallerDecompiler {

    CodePointer[] decompileCaller(Caller caller) throws IOException;

}
