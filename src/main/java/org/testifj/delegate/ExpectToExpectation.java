package org.testifj.delegate;

import org.testifj.Caller;
import org.testifj.Matcher;

import java.util.Optional;

public final class ExpectToExpectation implements Expectation {

    private final Caller caller;

    private final Object actualValue;

    private final Optional<Object> expectedValue;

    private final Matcher matcher;

    public ExpectToExpectation(Caller caller, Object actualValue, Optional<Object> expectedValue, Matcher matcher) {
        assert caller != null : "Caller can't be null";
        assert expectedValue != null : "Expected value can't be null";
        assert matcher != null : "Matcher can't be null";

        this.caller = caller;
        this.actualValue = actualValue;
        this.expectedValue = expectedValue;
        this.matcher = matcher;
    }

    public Caller getCaller() {
        return caller;
    }

    public Object getActualValue() {
        return actualValue;
    }

    public Optional<Object> getExpectedValue() {
        return expectedValue;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpectToExpectation that = (ExpectToExpectation) o;

        if (actualValue != null ? !actualValue.equals(that.actualValue) : that.actualValue != null) return false;
        if (!caller.equals(that.caller)) return false;
        if (!expectedValue.equals(that.expectedValue)) return false;
        if (!matcher.equals(that.matcher)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = caller.hashCode();
        result = 31 * result + (actualValue != null ? actualValue.hashCode() : 0);
        result = 31 * result + expectedValue.hashCode();
        result = 31 * result + matcher.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExpectToExpectation{" +
                "caller=" + caller +
                ", actualValue=" + actualValue +
                ", expectedValue=" + expectedValue +
                ", matcher=" + matcher +
                '}';
    }
}
