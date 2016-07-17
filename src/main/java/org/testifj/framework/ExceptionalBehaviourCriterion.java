package org.testifj.framework;

import org.testifj.Matcher;

@SuppressWarnings("uncehcked")
public class ExceptionalBehaviourCriterion implements BehaviouralCriterion {

    private final Matcher<Throwable> exceptionMatcher;

    public ExceptionalBehaviourCriterion(Matcher<Throwable> exceptionMatcher) {
        assert exceptionMatcher != null : "exceptionMatcher can't be null";
        this.exceptionMatcher = exceptionMatcher;
    }

    @Override
    public VerificationResult verify(Expectation expectation) {
        assert expectation != null : "expectation can't be null";

        if (!(expectation instanceof BehaviouralExpectation)) {
            return new UnsupportedVerificationResult(this);
        }

        final BehaviouralExpectation behaviouralExpectation = (BehaviouralExpectation) expectation;
        final BehavioralOutcome outcome = behaviouralExpectation.getOutcome();

        if (outcome.isExceptional()) {
            final boolean matches = exceptionMatcher.matches((Throwable) outcome.getResult().get());

            if (!matches) {
                return InvalidVerificationResult.single(this);
            } else {
                return ValidVerificationResult.single(this);
            }
        }

        return InvalidVerificationResult.single(this);
    }
}
