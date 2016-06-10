package org.testifj.framework;

import java.util.Optional;

public class ValueExpectation implements Expectation<Object> {

    private final Object subject;

    private final Object expectedValue;

    private final boolean hasExpectedValue;

    private final ExpectationReference expectationReference;

    private final Criterion criterion;

    private ValueExpectation(Object subject, Object expectedValue, boolean hasExpectedValue, ExpectationReference expectationReference, Criterion criterion) {
        this.subject = subject;
        this.expectedValue = expectedValue;
        this.hasExpectedValue = hasExpectedValue;
        this.expectationReference = expectationReference;
        this.criterion = criterion;
    }

    @Override
    public Object getSubject() {
        return null;
    }

    /**
     * Returns the expected value if the expected value is present. The expected value is only present
     * when a direct comparison between objects are performed, e.g. <code>expect("foo").toEqual("bar");</code>.
     *
     * @return The expected value, if available or an empty optional otherwise.
     */
    public Optional<Object> getExpectedValue() {
        return null;
    }

    @Override
    public ExpectationReference getExpectationReference() {
        return null;
    }

    @Override
    public Criterion getCriterion() {
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Object subject;
        private Object expectedValue;
        private boolean hasExpectedValue;
        private ExpectationReference expectationReference;
        private Criterion criterion;

        public Builder subject(Object subject) {
            this.subject = subject;
            return this;
        }

        public Builder expectedValue(Object expectedValue) {
            this.expectedValue = expectedValue;
            this.hasExpectedValue = true;
            return this;
        }

        public Builder expectationReference(ExpectationReference expectationReference) {
            this.expectationReference = expectationReference;
            return this;
        }

        public Builder criterion(Criterion criterion) {
            this.criterion = criterion;
            return this;
        }

        public ValueExpectation build() {
            if (expectationReference == null) {
                throw new IllegalStateException("expectationReference must be provided");
            }

            if (criterion == null) {
                throw new IllegalStateException("criterion must be provided");
            }

            return new ValueExpectation(subject, expectedValue, hasExpectedValue, expectationReference, criterion);
        }
    }

}
