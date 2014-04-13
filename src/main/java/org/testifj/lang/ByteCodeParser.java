package org.testifj.lang;

import org.testifj.lang.model.Element;

import java.io.IOException;
import java.io.InputStream;

public interface ByteCodeParser {

    Element[] parse(Method method, InputStream in) throws IOException;

}
