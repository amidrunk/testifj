package org.testifj.lang.model;

import java.util.Map;

public interface ElementMetaData {

    public static final String LINE_NUMBER = "LineNumber";

    public static final String PROGRAM_COUNTER = "pc";

    void setAttribute(String key, Object value);

    Object getAttribute(String key);

    Map<String, Object> getAttributes();

}
