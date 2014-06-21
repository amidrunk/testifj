package org.testifj.lang.decompile;

import org.testifj.lang.classfile.Method;
import org.testifj.lang.model.Element;

import java.io.IOException;

public interface Decompiler {

    Element[] parse(Method method, CodeStream codeStream) throws IOException;

    Element[] parse(Method method, CodeStream codeStream, DecompilationProgressCallback callback) throws IOException;

}
