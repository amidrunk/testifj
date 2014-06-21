package org.testifj.lang.classfile;

import java.io.IOException;
import java.io.InputStream;

public interface ClassFileReader {

    ClassFile read(InputStream in) throws IOException, ClassFormatError;

}
