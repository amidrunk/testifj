package org.testifj.lang.classfile;

import org.testifj.lang.decompile.ConstantPoolEntryDescriptor;
import org.testifj.lang.decompile.ConstantPoolEntryTag;

public interface MethodRefDescriptor extends ConstantPoolEntryDescriptor {

    String getClassName();

    String getMethodName();

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.METHOD_REF;
    }

}
