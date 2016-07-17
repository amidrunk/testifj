package org.testifj;

import org.testifj.framework.ExpectationContext;
import org.testifj.matchers.core.ArrayThatIs;

public class ArrayExpectations<T> extends ExpectationsBase<T[], ArrayExpectations<T>> {

    public ArrayExpectations(ExpectationContext<T[]> context) {
        super(context);
    }

    public void toHaveLength(int length) {
        reportValueExpectation(ArrayThatIs.ofLength(length));
    }

    @Override
    protected ArrayExpectations<T> newInstance(ExpectationContext<T[]> context) {
        return new ArrayExpectations<T>(context);
    }
}
