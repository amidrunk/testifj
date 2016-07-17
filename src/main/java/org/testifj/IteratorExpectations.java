package org.testifj;

import org.testifj.framework.ExpectationContext;
import org.testifj.matchers.core.IteratorThatIs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

public class IteratorExpectations<T> extends ExpectationsBase<Iterator<T>, IteratorExpectations<T>> {

    public IteratorExpectations(ExpectationContext<Iterator<T>> context) {
        super(context);
    }

    public void toHaveNext() {
        reportValueExpectation(Iterator::hasNext);
    }

    public void toContain(T ... values) {
        final Optional expected = Optional.of(values);
        reportValueExpectation(IteratorThatIs.iteratorOf(values), expected);
    }

    @Override
    protected IteratorExpectations<T> newInstance(ExpectationContext<Iterator<T>> context) {
        return new IteratorExpectations<T>(context);
    }
}
