package org.testifj.lang.classfile;

public interface InvokeDynamicDescriptor extends ConstantPoolEntryDescriptor {

    int getBootstrapMethodAttributeIndex();

    String getMethodName();

    String getMethodDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.INVOKE_DYNAMIC;
    }

}
