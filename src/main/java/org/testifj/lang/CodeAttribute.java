package org.testifj.lang;

public interface CodeAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "Code";

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
