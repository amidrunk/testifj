package org.testifj;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ValueMismatchFailureImpl implements ValueMismatchFailure {

    private final List<StackTraceElement> callStack;

    private final Matcher<?> matcher;

    private final Optional<Object> expectedValue;

    private final Object value;

    public ValueMismatchFailureImpl(StackTraceElement[] callStack, Matcher<?> matcher, Optional<Object> expectedValue, Object value) {

        assert callStack != null : "Call stack can't be null";
        assert matcher != null : "Matcher can't be null";

        this.callStack = Arrays.asList(callStack);
        this.matcher = matcher;
        this.expectedValue = expectedValue;
        this.value = value;
    }

    @Override
    public Optional<Object> getExpectedValue() {
        return expectedValue;
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
        if (!expectedValue.equals(that.expectedValue)) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = callStack.hashCode();
        result = 31 * result + matcher.hashCode();
        result = 31 * result + (expectedValue != null ? expectedValue.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ValueMismatchFailureImpl{" +
                "callStack=" + callStack +
                ", matcher=" + matcher +
                ", expectedValue=" + expectedValue +
                ", value=" + value +
                '}';
    }
}
