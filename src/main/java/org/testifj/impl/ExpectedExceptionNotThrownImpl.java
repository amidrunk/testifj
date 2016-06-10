package org.testifj.impl;

import io.recode.Caller;
import org.testifj.ExpectedExceptionNotThrown;

public final class ExpectedExceptionNotThrownImpl implements ExpectedExceptionNotThrown {

    private final Caller caller;

    private final Class<? extends Throwable> expectedException;

    public ExpectedExceptionNotThrownImpl(Caller caller, Class<? extends Throwable> expectedException) {
        assert caller != null : "Caller can't be null";
        assert expectedException != null : "Expected exception can't be null";

        this.caller = caller;
        this.expectedException = expectedException;
    }

    @Override
    public Class<? extends Throwable> getExpectedException() {
        return expectedException;
    }

    @Override
    public Caller getCaller() {
        return caller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpectedExceptionNotThrownImpl that = (ExpectedExceptionNotThrownImpl) o;

        if (!caller.equals(that.caller)) return false;
        if (!expectedException.equals(that.expectedException)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = caller.hashCode();
        result = 31 * result + expectedException.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExpectedExceptionNotThrownImpl{" +
                "caller=" + caller +
                ", expectedException=" + expectedException +
                '}';
    }
}
