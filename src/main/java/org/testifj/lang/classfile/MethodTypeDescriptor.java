package org.testifj.lang.classfile;

public interface MethodTypeDescriptor extends ConstantPoolEntryDescriptor {

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.METHOD_TYPE;
    }

}
