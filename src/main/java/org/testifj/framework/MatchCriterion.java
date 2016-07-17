package org.testifj.framework;

import org.testifj.Matcher;

public class MatchCriterion implements Criterion {

    private final Matcher<?> matcher;

    public MatchCriterion(Matcher<?> matcher) {
        assert matcher != null : "matcher can't be null";
        this.matcher = matcher;
    }

    @Override
    @SuppressWarnings("unchecked")
    public VerificationResult verify(Expectation<?> expectation) {
        assert expectation != null : "expectation can't be null";

        if (!(expectation instanceof ValueExpectation)) {
            return new UnsupportedVerificationResult(this);
        }

        if (!((Matcher) matcher).matches(expectation.getSubject())) {
            return InvalidVerificationResult.single(this);
        }

        return ValidVerificationResult.single(this);
    }
}
