package org.testifj.lang.classfile;

import org.testifj.lang.decompile.ConstantPoolEntryDescriptor;
import org.testifj.lang.decompile.ConstantPoolEntryTag;

public interface FieldRefDescriptor extends ConstantPoolEntryDescriptor {

    String getClassName();

    String getDescriptor();

    String getName();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.FIELD_REF;
    }

}
