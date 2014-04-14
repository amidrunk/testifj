package org.testifj;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Caller {

    private final List<StackTraceElement> stackTraceElements;

    private final int callerStackTraceIndex;

    public Caller(List<StackTraceElement> stackTraceElements, int callerStackTraceIndex) {
        assert stackTraceElements != null : "Stack trace elements can't be null";
        assert callerStackTraceIndex >= 0 && callerStackTraceIndex < stackTraceElements.size() : "Caller index must be in [0, " + stackTraceElements.size() + ")";

        this.stackTraceElements = stackTraceElements;
        this.callerStackTraceIndex = callerStackTraceIndex;
    }

    public List<StackTraceElement> getCallStack() {
        return Collections.unmodifiableList(stackTraceElements);
    }

    public StackTraceElement getCallerStackTraceElement() {
        return stackTraceElements.get(callerStackTraceIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Caller caller = (Caller) o;

        if (callerStackTraceIndex != caller.callerStackTraceIndex) return false;
        if (!stackTraceElements.equals(caller.stackTraceElements)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stackTraceElements.hashCode();
        result = 31 * result + callerStackTraceIndex;
        return result;
    }

    @Override
    public String toString() {
        return "Caller{" +
                "stackTraceElements=" + stackTraceElements +
                ", callerStackTraceIndex=" + callerStackTraceIndex +
                '}';
    }
}
