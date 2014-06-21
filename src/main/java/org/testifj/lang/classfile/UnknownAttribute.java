package org.testifj.lang.classfile;

import org.testifj.lang.classfile.Attribute;

import java.io.InputStream;

public interface UnknownAttribute extends Attribute {

    InputStream getData();

}
