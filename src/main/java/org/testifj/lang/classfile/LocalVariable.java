package org.testifj.lang.classfile;

import java.lang.reflect.Type;

public interface LocalVariable {

    int getStartPC();

    int getLength();

    String getName();

    Type getType();

    int getIndex();

}
