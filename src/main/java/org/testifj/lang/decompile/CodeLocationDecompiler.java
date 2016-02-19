package org.testifj.lang.decompile;

import org.testifj.Caller;
import org.testifj.CodeLocation;

import java.io.IOException;

/**
 * A <code>CodeLocationDecompiler</code> decompiles a specific line in a class file and returns an
 * abstract syntax tree of the calling code.
 */
public interface CodeLocationDecompiler {

    CodePointer[] decompileCodeLocation(CodeLocation codeLocation) throws IOException;

    CodePointer[] decompileCodeLocation(CodeLocation codeLocation, DecompilationProgressCallback callback) throws IOException;

}
