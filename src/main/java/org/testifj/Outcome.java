package org.testifj;

import io.recode.Caller;

import java.util.Optional;

public final class Outcome {

    private final Caller caller;

    private final Optional<Throwable> exception;

    private Outcome(Caller caller, Optional<Throwable> optionalThrowable) {
        this.caller = caller;
        this.exception = optionalThrowable;
    }

    public Optional<Throwable> getException() {
        return exception;
    }

    public boolean isExceptional() {
        return exception.isPresent();
    }

    public Caller getCaller() {
        return caller;
    }

    public static Outcome exceptional(Caller caller, Throwable exception) {
        assert caller != null : "Caller can't be null";
        assert exception != null : "Exception can't be null for exceptional outcomes";

        return new Outcome(caller, Optional.of(exception));
    }

    public static Outcome successful(Caller caller) {
        assert caller != null : "Caller can't be null";
        return new Outcome(caller, Optional.empty());
    }
}
