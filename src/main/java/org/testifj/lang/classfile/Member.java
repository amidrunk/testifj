package org.testifj.lang.classfile;

import org.testifj.lang.classfile.Attribute;
import org.testifj.lang.classfile.ClassFile;

import java.util.List;

public interface Member {

    int getAccessFlags();

    String getName();

    List<Attribute> getAttributes();

    ClassFile getClassFile();

}
