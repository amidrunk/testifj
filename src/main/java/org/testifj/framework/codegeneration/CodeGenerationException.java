package org.testifj.framework.codegeneration;

public class CodeGenerationException extends RuntimeException {

    public CodeGenerationException() {
    }

    public CodeGenerationException(String message) {
        super(message);
    }

    public CodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeGenerationException(Throwable cause) {
        super(cause);
    }

    public CodeGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
