package org.testifj.lang;

import org.testifj.lang.model.Element;

import java.io.IOException;
import java.io.InputStream;

public interface Decompiler {

    Element[] parse(Method method, CodeStream codeStream) throws IOException;

    Element[] parse(Method method, CodeStream codeStream, DecompilationProgressCallback callback) throws IOException;

}
