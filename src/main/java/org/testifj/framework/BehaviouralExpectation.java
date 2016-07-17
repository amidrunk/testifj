package org.testifj.framework;

import org.testifj.Procedure;

public class BehaviouralExpectation implements Expectation<Procedure> {

    private final Procedure subject;

    private final ExpectationReference expectationReference;

    private final BehaviouralCriterion criterion;

    private final BehavioralOutcome outcome;

    private BehaviouralExpectation(Procedure subject,
                                   ExpectationReference expectationReference,
                                   BehaviouralCriterion criterion,
                                   BehavioralOutcome outcome) {
        this.subject = subject;
        this.expectationReference = expectationReference;
        this.criterion = criterion;
        this.outcome = outcome;
    }

    @Override
    public Procedure getSubject() {
        return subject;
    }

    @Override
    public ExpectationReference getExpectationReference() {
        return expectationReference;
    }

    @Override
    public Criterion getCriterion() {
        return criterion;
    }

    public BehavioralOutcome getOutcome() {
        return outcome;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Procedure subject;

        private ExpectationReference expectationReference;

        private BehaviouralCriterion criterion;

        private BehavioralOutcome actualOutcome;

        public Builder subject(Procedure subject) {
            this.subject = subject;
            return this;
        }

        public Builder expectationReference(ExpectationReference expectationReference) {
            this.expectationReference = expectationReference;
            return this;
        }

        public Builder criterion(BehaviouralCriterion criterion) {
            this.criterion = criterion;
            return this;
        }

        public Builder outcome(BehavioralOutcome actualOutcome) {
            this.actualOutcome = actualOutcome;
            return this;
        }

        public BehaviouralExpectation build() {
            if (subject == null) {
                throw new IllegalStateException("subject can't be null");
            }

            if (expectationReference == null) {
                throw new IllegalStateException("expectationReference can't be null");
            }

            if (criterion == null) {
                throw new IllegalStateException("criterion can't be null");
            }

            if (actualOutcome == null) {
                throw new IllegalStateException("outcome can't be null");
            }

            return new BehaviouralExpectation(subject, expectationReference, criterion, actualOutcome);
        }
    }

}
