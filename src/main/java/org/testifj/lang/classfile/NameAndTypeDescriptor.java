package org.testifj.lang.classfile;

import org.testifj.lang.decompile.ConstantPoolEntryDescriptor;
import org.testifj.lang.decompile.ConstantPoolEntryTag;

public interface NameAndTypeDescriptor extends ConstantPoolEntryDescriptor {

    String getName();

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.NAME_AND_TYPE;
    }

}
