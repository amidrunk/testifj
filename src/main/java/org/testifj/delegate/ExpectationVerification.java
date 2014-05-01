package org.testifj.delegate;

import org.testifj.Description;

import java.util.Optional;

public interface ExpectationVerification {
    ExpectationVerificationContext<? extends Expectation> getExpectation();

    boolean isCompliant();

    Description getExpectationDescription();

    Optional<Description> getVerificationFailureDescription();
}
