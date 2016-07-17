package org.testifj.framework;

public class ExpectationContext<T> {

    private final T subject;

    private final ExpectationReference expectationReference;

    private final boolean inverted;

    public ExpectationContext(T subject, ExpectationReference expectationReference) {
        this(subject, expectationReference, false);
    }

    public ExpectationContext(T subject, ExpectationReference expectationReference, boolean inverted) {
        assert expectationReference != null : "expectationReference can't be null";

        this.subject = subject;
        this.expectationReference = expectationReference;
        this.inverted = inverted;
    }

    public T getSubject() {
        return subject;
    }

    public ExpectationReference getExpectationReference() {
        return expectationReference;
    }

    public boolean isInverted() {
        return inverted;
    }

    public ExpectationContext<T> invert() {
        return new ExpectationContext<T>(subject, expectationReference, !inverted);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpectationContext<?> that = (ExpectationContext<?>) o;

        if (!subject.equals(that.subject)) return false;
        return expectationReference.equals(that.expectationReference);

    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + expectationReference.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExpectationContext{" +
                "subject=" + subject +
                ", expectationReference=" + expectationReference +
                '}';
    }
}
