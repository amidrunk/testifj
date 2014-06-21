package org.testifj.lang.classfile;

import org.testifj.lang.decompile.ConstantPoolEntryDescriptor;
import org.testifj.lang.decompile.ConstantPoolEntryTag;

public interface InvokeDynamicDescriptor extends ConstantPoolEntryDescriptor {

    int getBootstrapMethodAttributeIndex();

    String getMethodName();

    String getMethodDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.INVOKE_DYNAMIC;
    }

}
