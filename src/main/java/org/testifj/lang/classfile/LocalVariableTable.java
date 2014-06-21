package org.testifj.lang.classfile;

import org.testifj.lang.classfile.Attribute;
import org.testifj.lang.classfile.LocalVariable;

import java.util.List;

public interface LocalVariableTable extends Attribute {

    String ATTRIBUTE_NAME = "LocalVariableTable";

    List<LocalVariable> getLocalVariables();

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
