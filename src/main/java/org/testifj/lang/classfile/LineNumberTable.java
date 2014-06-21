package org.testifj.lang.classfile;

import org.testifj.lang.Range;

import java.util.List;

public interface LineNumberTable extends Attribute {

    String ATTRIBUTE_NAME = "LineNumberTable";

    List<LineNumberTableEntry> getEntries();

    Range getSourceFileRange();

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
