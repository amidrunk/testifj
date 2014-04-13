package org.testifj.lang;

import java.io.InputStream;

public interface Method extends Member {

    CodeAttribute getCode();

    InputStream getCodeForLineNumber(int lineNumber);

    LocalVariable getLocalVariableForIndex(int index);

}
