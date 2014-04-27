package org.testifj.lang.model;

import java.util.Map;

public interface ElementMetaData {

    public static final String LINE_NUMBER = "LineNumber";

    void setAttribute(String key, Object value);

    Object getAttribute(String key);

    Map<String, Object> getAttributes();

}
