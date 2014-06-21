package org.testifj.lang.classfile;

import org.testifj.lang.decompile.ConstantPoolEntryTag;

public interface InterfaceMethodRefDescriptor extends MethodRefDescriptor {

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.INTERFACE_METHOD_REF;
    }

}
