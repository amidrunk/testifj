package org.testifj;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValueMismatchFailureImpl implements ValueMismatchFailure {

    private final List<StackTraceElement> callStack;

    private final Matcher<?> matcher;

    private final Object value;

    public ValueMismatchFailureImpl(StackTraceElement[] callStack, Matcher<?> matcher, Object value) {
        assert callStack != null : "Call stack can't be null";
        assert matcher != null : "Matcher can't be null";

        this.callStack = Arrays.asList(callStack);
        this.matcher = matcher;
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Matcher<?> getMatcher() {
        return matcher;
    }

    @Override
    public List<StackTraceElement> getCallStack() {
        return Collections.unmodifiableList(callStack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueMismatchFailureImpl that = (ValueMismatchFailureImpl) o;

        if (!callStack.equals(that.callStack)) return false;
        if (!matcher.equals(that.matcher)) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = callStack.hashCode();
        result = 31 * result + matcher.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ValueMismatchFailureImpl{" +
                "callStack=" + callStack +
                ", matcher=" + matcher +
                ", value=" + value +
                '}';
    }
}
