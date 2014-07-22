package org.testifj.lang.classfile;

public interface MethodRefDescriptor extends ConstantPoolEntryDescriptor {

    String getClassName();

    String getMethodName();

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.METHOD_REF;
    }

}
