package org.testifj.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidVerificationResult implements VerificationResult {

    private final Criterion criterion;

    private final List<VerificationResult> subVerificationResults;

    private ValidVerificationResult(Criterion criterion, List<VerificationResult> subVerificationResults) {
        this.criterion = criterion;
        this.subVerificationResults = subVerificationResults;
    }

    @Override
    public Criterion getCriterion() {
        return criterion;
    }

    @Override
    public List<VerificationResult> getSubVerificationResults() {
        return subVerificationResults;
    }

    @Override
    public VerificationOutcome getOutcome() {
        return VerificationOutcome.VALID;
    }

    public static ValidVerificationResult single(Criterion criterion) {
        assert criterion != null : "criterion can't be null";

        return new ValidVerificationResult(criterion, Collections.emptyList());
    }

    public static ValidVerificationResult composite(Criterion criterion, List<VerificationResult> subVerificationResults) {
        assert criterion != null : "criterion can't be null";
        assert subVerificationResults != null : "subVerificationResults can't be null";

        return new ValidVerificationResult(criterion, Collections.unmodifiableList(new ArrayList<>(subVerificationResults)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidVerificationResult that = (ValidVerificationResult) o;

        if (!criterion.equals(that.criterion)) return false;
        return subVerificationResults.equals(that.subVerificationResults);

    }

    @Override
    public int hashCode() {
        int result = criterion.hashCode();
        result = 31 * result + subVerificationResults.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ValidVerificationResult{" +
                "criterion=" + criterion +
                ", subVerificationResults=" + subVerificationResults +
                '}';
    }
}
