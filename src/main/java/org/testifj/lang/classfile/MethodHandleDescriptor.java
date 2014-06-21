package org.testifj.lang.classfile;

import org.testifj.lang.decompile.ConstantPoolEntryDescriptor;
import org.testifj.lang.decompile.ConstantPoolEntryTag;

public interface MethodHandleDescriptor extends ConstantPoolEntryDescriptor {

    ReferenceKind getReferenceKind();

    String getClassName();

    String getMethodName();

    String getMethodDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.METHOD_HANDLE;
    }

}
