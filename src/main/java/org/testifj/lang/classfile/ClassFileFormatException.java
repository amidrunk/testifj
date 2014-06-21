package org.testifj.lang.classfile;

public class ClassFileFormatException extends RuntimeException {

    public ClassFileFormatException(String message) {
        super(message);
    }

    public ClassFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
