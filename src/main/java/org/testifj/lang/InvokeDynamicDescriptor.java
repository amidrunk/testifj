package org.testifj.lang;

public interface InvokeDynamicDescriptor extends ConstantPoolEntryDescriptor {

    int getBootstrapMethodAttributeIndex();

    String getMethodName();

    String getMethodDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.INVOKE_DYNAMIC;
    }

}
