package org.testifj.lang;

import java.io.InputStream;

public interface CodeAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "Code";

    int getMaxStack();

    int getMaxLocals();

    InputStream getCode();

    // TODO exception table

    // TODO code attributes

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
