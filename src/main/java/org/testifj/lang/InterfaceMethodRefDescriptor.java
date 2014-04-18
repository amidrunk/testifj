package org.testifj.lang;

public interface InterfaceMethodRefDescriptor extends ConstantPoolEntryDescriptor {

    String getClassName();

    String getMethodName();

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.INTERFACE_METHOD_REF;
    }

}
