package org.testifj.lang;

public interface NameAndTypeDescriptor extends ConstantPoolEntryDescriptor {

    String getName();

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.NAME_AND_TYPE;
    }

}
