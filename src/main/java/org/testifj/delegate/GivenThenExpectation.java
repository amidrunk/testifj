package org.testifj.delegate;

import org.testifj.Action;
import io.recode.Caller;

public final class GivenThenExpectation implements Expectation {

    private final Caller caller;

    private final Object providedValue;

    private final Action<Object> verificationAction;

    public GivenThenExpectation(Caller caller, Object providedValue, Action<Object> verificationAction) {
        assert caller != null : "Caller can't be null";
        assert verificationAction != null : "Verification action can't be null";

        this.caller = caller;
        this.providedValue = providedValue;
        this.verificationAction = verificationAction;
    }

    @Override
    public Caller getCaller() {
        return caller;
    }

    public Object getProvidedValue() {
        return providedValue;
    }

    public Action<Object> getVerificationAction() {
        return verificationAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GivenThenExpectation that = (GivenThenExpectation) o;

        if (!caller.equals(that.caller)) return false;
        if (!providedValue.equals(that.providedValue)) return false;
        if (!verificationAction.equals(that.verificationAction)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = caller.hashCode();
        result = 31 * result + providedValue.hashCode();
        result = 31 * result + verificationAction.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GivenThenExpectation{" +
                "caller=" + caller +
                ", providedValue=" + providedValue +
                ", verificationAction=" + verificationAction +
                '}';
    }
}
