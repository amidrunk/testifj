package org.testifj;

import org.testifj.framework.ExpectationContext;
import org.testifj.matchers.core.IntegerThatIs;

public class IntegerExpectations extends ExpectationsBase<Integer, IntegerExpectations> {

    public IntegerExpectations(ExpectationContext<Integer> context) {
        super(context);
    }

    public void toBeLessThan(int reference) {
        reportValueExpectation(IntegerThatIs.lessThan(reference));
    }

    public void toBeGreaterThan(int reference) {
        reportValueExpectation(IntegerThatIs.greaterThan(reference));
    }

    @Override
    protected IntegerExpectations newInstance(ExpectationContext<Integer> context) {
        return new IntegerExpectations(context);
    }
}
