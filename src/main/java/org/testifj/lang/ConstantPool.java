package org.testifj.lang;

import java.util.List;

public interface ConstantPool {

    List<ConstantPoolEntry> getEntries();

    String getClassName(int index);

    String getString(int index);

    ConstantPoolEntry getEntry(int index);

}
