package org.testifj.lang;

import java.lang.reflect.Type;

public interface LocalVariable {

    int getStartPC();

    int getLength();

    String getVariableName();

    Type getVariableType();

    int getIndex();

}
