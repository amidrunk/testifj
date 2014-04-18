package org.testifj;

import java.util.Optional;

public class ValueMismatchFailureImpl implements ValueMismatchFailure {

    private final Caller caller;

    private final Matcher<?> matcher;

    private final Optional<Object> expectedValue;

    private final Object value;

    public ValueMismatchFailureImpl(Caller caller, Matcher<?> matcher, Optional<Object> expectedValue, Object value) {
        assert caller != null : "Caller can't be null";
        assert matcher != null : "Matcher can't be null";

        this.caller = caller;
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

    public Caller getCaller() {
        return caller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueMismatchFailureImpl that = (ValueMismatchFailureImpl) o;

        if (!caller.equals(that.caller)) return false;
        if (!matcher.equals(that.matcher)) return false;
        if (!expectedValue.equals(that.expectedValue)) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = caller.hashCode();
        result = 31 * result + matcher.hashCode();
        result = 31 * result + (expectedValue != null ? expectedValue.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ValueMismatchFailureImpl{" +
                "caller=" + caller +
                ", matcher=" + matcher +
                ", expectedValue=" + expectedValue +
                ", value=" + value +
                '}';
    }
}
