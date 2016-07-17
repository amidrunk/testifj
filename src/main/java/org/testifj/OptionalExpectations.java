package org.testifj;

import org.testifj.framework.ExpectationContext;
import org.testifj.matchers.core.OptionalThatIs;

import java.util.Optional;

public class OptionalExpectations<T> extends ExpectationsBase<Optional<T>, OptionalExpectations<T>> {

    public OptionalExpectations(ExpectationContext<Optional<T>> context) {
        super(context);
    }

    public void toBeEmpty() {
        reportValueExpectation(OptionalThatIs.empty());
    }

    public void toContain(T value) {
        reportValueExpectation(OptionalThatIs.optionalOf(value));
    }

    @Override
    protected OptionalExpectations<T> newInstance(ExpectationContext<Optional<T>> context) {
        return new OptionalExpectations<T>(context);
    }
}
