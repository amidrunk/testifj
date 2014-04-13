package org.testifj.lang;

import java.io.InputStream;
import java.util.List;

public interface CodeAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "Code";

    int getMaxStack();

    int getMaxLocals();

    InputStream getCode();

    List<ExceptionTableEntry> getExceptionTable();

    List<Attribute> getAttributes();

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
