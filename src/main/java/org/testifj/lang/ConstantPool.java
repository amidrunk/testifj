package org.testifj.lang;

import java.util.List;

public interface ConstantPool {

    List<ConstantPoolEntry> getEntries();

    String getClassName(int index);

    String getString(int index);

    ConstantPoolEntry getEntry(int index);

    <T extends ConstantPoolEntry> T getEntry(int index, Class<T> type);

    ConstantPoolEntry[] getEntries(int[] indices);

    FieldDescriptor getFieldDescriptor(int index);

}
