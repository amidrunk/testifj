package org.testifj.lang.classfile;

import org.testifj.lang.decompile.ConstantPoolEntryDescriptor;
import org.testifj.lang.decompile.ConstantPoolEntryTag;

public interface MethodTypeDescriptor extends ConstantPoolEntryDescriptor {

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.METHOD_TYPE;
    }

}
