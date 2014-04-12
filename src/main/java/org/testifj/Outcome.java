package org.testifj;

public final class Outcome {

    private final Throwable exception;

    private Outcome(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        if (!isExceptional()) {
            throw new IllegalStateException("Outcome is not exceptional");
        }

        return exception;
    }

    public boolean isExceptional() {
        return (exception != null);
    }

    public static Outcome exceptional(Throwable exception) {
        assert exception != null : "Exception can't be null for exceptional outcomes";

        return new Outcome(exception);
    }

    public static Outcome successful() {
        return new Outcome(null);
    }

}
