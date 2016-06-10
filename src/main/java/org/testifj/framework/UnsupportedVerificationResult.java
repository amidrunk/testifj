package org.testifj.framework;

import java.util.Collections;
import java.util.List;

public class UnsupportedVerificationResult implements VerificationResult {

    private final Criterion criterion;

    public UnsupportedVerificationResult(Criterion criterion) {
        assert criterion != null : "criterion can't be null";
        this.criterion = criterion;
    }

    @Override
    public Criterion getCriterion() {
        return criterion;
    }

    @Override
    public VerificationOutcome getOutcome() {
        return VerificationOutcome.NOT_SUPPORTED;
    }

    @Override
    public List<VerificationResult> getSubVerificationResults() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnsupportedVerificationResult that = (UnsupportedVerificationResult) o;

        return criterion.equals(that.criterion);

    }

    @Override
    public int hashCode() {
        return criterion.hashCode();
    }

    @Override
    public String toString() {
        return "UnsupportedVerificationResult{" +
                "criterion=" + criterion +
                '}';
    }
}
