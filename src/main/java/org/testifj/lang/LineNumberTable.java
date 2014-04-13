package org.testifj.lang;

import java.util.List;

public interface LineNumberTable extends Attribute {

    String ATTRIBUTE_NAME = "LineNumberTable";

    List<LineNumberTableEntry> getEntries();

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
