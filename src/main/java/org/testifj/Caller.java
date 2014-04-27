package org.testifj;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Caller {

    public static final int CALLER_STACK_TRACE_INDEX = 2;
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

    public Caller getCaller() {
        return new Caller(stackTraceElements, callerStackTraceIndex + 1);
    }

    public Optional<Caller> scan(Predicate<StackTraceElement> predicate) {
        assert predicate != null : "Predicate can't be null";

        int currentIndex = callerStackTraceIndex + 1;

        while (currentIndex < stackTraceElements.size()) {
            if (predicate.test(stackTraceElements.get(currentIndex))) {
                return Optional.of(new Caller(stackTraceElements, currentIndex));
            }

            currentIndex++;
        }

        return Optional.empty();
    }

    public int getCallerStackTraceIndex() {
        return callerStackTraceIndex;
    }

    public static Caller me() {
        return new Caller(Arrays.asList(Thread.currentThread().getStackTrace()), CALLER_STACK_TRACE_INDEX);
    }

    public static Caller adjacent(int offset) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final StackTraceElement callerStackElement = stackTrace[CALLER_STACK_TRACE_INDEX];

        stackTrace[CALLER_STACK_TRACE_INDEX] = new StackTraceElement(
                callerStackElement.getClassName(),
                callerStackElement.getMethodName(),
                callerStackElement.getFileName(),
                callerStackElement.getLineNumber() + offset
        );

        return new Caller(Arrays.asList(stackTrace), CALLER_STACK_TRACE_INDEX);
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
                "stackTraceElements=StackTraceElement[" + stackTraceElements.size() + "]" +
                ", callerStackTraceIndex=" + callerStackTraceIndex +
                ", callerStackTraceElement=" + getCallerStackTraceElement() +
                '}';
    }
}
