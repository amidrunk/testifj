package org.testifj.lang;

import org.testifj.lang.model.Signature;

import java.io.InputStream;
import java.util.Optional;

public interface Method extends Member {

    CodeAttribute getCode();

    InputStream getCodeForLineNumber(int lineNumber);

    LocalVariable getLocalVariableForIndex(int index);

    Optional<LocalVariableTable> getLocalVariableTable();

    Signature getSignature();

}
