package org.testifj.lang;

public final class InvocationTargetRuntimeException extends RuntimeException {

    public InvocationTargetRuntimeException(String message) {
        super(message);
    }

    public InvocationTargetRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvocationTargetRuntimeException(Throwable cause) {
        super(cause);
    }
}
