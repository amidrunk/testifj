package org.testifj.lang;

import org.testifj.Caller;

import java.io.IOException;

public interface CallerDecompiler {

    CodePointer[] decompileCaller(Caller caller) throws IOException;

}
