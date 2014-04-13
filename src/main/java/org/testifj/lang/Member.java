package org.testifj.lang;

import java.util.List;

public interface Member {

    int getAccessFlags();

    String getName();

    String getSignature();

    List<Attribute> getAttributes();

    ClassFile getClassFile();

}
