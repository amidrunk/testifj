package org.testifj.delegate;

import org.testifj.ServiceContext;

import java.util.List;

public final class ExpectationVerificationContextImpl<T extends Expectation> implements ExpectationVerificationContext<T> {

    private final T expectation;

    private final List<ExpectationVerificationContext> childNodes;

    private final ServiceContext serviceContext;

    public ExpectationVerificationContextImpl(T expectation,
                                              List<ExpectationVerificationContext> childNodes,
                                              ServiceContext serviceContext) {
        assert expectation != null : "Expectation can't be null";
        assert childNodes != null : "Child nodes can't be null";
        assert serviceContext != null : "Service context can't be null";

        this.expectation = expectation;
        this.childNodes = childNodes;
        this.serviceContext = serviceContext;
    }

    @Override
    public T getExpectation() {
        return expectation;
    }

    @Override
    public List<ExpectationVerificationContext> getSubContexts() {
        return childNodes;
    }

    @Override
    public <T> T get(Class<T> type) {
        return serviceContext.get(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpectationVerificationContextImpl that = (ExpectationVerificationContextImpl) o;

        if (!childNodes.equals(that.childNodes)) return false;
        if (!expectation.equals(that.expectation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = expectation.hashCode();
        result = 31 * result + childNodes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExpectationVerificationContext{" +
                "expectation=" + expectation +
                ", childNodes=" + childNodes +
                '}';
    }
}
