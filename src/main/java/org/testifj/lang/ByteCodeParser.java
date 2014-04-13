package org.testifj.lang;

import org.testifj.lang.model.Element;

import java.io.IOException;
import java.io.InputStream;

public interface ByteCodeParser {

    Element[] parse(ClassFile classFile, InputStream in) throws IOException;

}
