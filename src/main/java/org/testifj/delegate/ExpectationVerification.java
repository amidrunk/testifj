package org.testifj.delegate;

import org.testifj.Description;

import java.util.Optional;

public final class ExpectationVerification {

    private final ExpectationVerificationContext<? extends Expectation> expectation;

    private final boolean compliant;

    private final Description expectationDescription;

    private final Optional<Description> verificationFailureDescription;

    private ExpectationVerification(ExpectationVerificationContext<? extends Expectation> expectation,
                                    boolean compliant,
                                    Description expectationDescription,
                                    Optional<Description> verificationFailureDescription) {
        this.expectation = expectation;
        this.compliant = compliant;
        this.expectationDescription = expectationDescription;
        this.verificationFailureDescription = verificationFailureDescription;
    }

    public ExpectationVerificationContext<? extends Expectation> getExpectation() {
        return expectation;
    }

    public boolean isCompliant() {
        return compliant;
    }

    public Description getExpectationDescription() {
        return expectationDescription;
    }

    public Optional<Description> getVerificationFailureDescription() {
        return verificationFailureDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpectationVerification that = (ExpectationVerification) o;

        if (compliant != that.compliant) return false;
        if (!expectation.equals(that.expectation)) return false;
        if (!expectationDescription.equals(that.expectationDescription)) return false;
        if (!verificationFailureDescription.equals(that.verificationFailureDescription)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = expectation.hashCode();
        result = 31 * result + (compliant ? 1 : 0);
        result = 31 * result + expectationDescription.hashCode();
        result = 31 * result + verificationFailureDescription.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExpectationVerification{" +
                "expectation=" + expectation +
                ", compliant=" + compliant +
                ", expectationDescription=" + expectationDescription +
                ", verificationFailureDescription=" + verificationFailureDescription +
                '}';
    }

    public static ExpectationVerification compliant(ExpectationVerificationContext<? extends Expectation> expectation, Description description) {
        assert expectation != null : "Expectation can't be null";
        assert description != null : "Description can't be null";

        return new ExpectationVerification(expectation, true, description, Optional.empty());
    }

    public static ExpectationVerification notCompliant(ExpectationVerificationContext<? extends Expectation> expectation,
                                                       Description description,
                                                       Description verificationFailureDescription) {
        assert expectation != null : "Expectation can't be null";
        assert description != null : "Description can't be null";
        assert verificationFailureDescription != null : "Verification failure description can't be null";

        return new ExpectationVerification(expectation, false, description, Optional.of(verificationFailureDescription));
    }
}
