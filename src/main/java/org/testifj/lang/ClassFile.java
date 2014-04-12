package org.testifj.lang;

import java.util.List;

public interface ClassFile {

    int getMinorVersion();

    int getMajorVersion();

    ConstantPool getConstantPool();

    int getAccessFlags();

    String getName();

    String getSuperClassName();

    List<String> getInterfaceNames();

    List<Field> getFields();

    List<Method> getMethods();

    List<Constructor> getConstructors();

    List<Attribute> getAttributes();


}
