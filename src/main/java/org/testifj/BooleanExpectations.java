package org.testifj;

import org.testifj.framework.ExpectationContext;
import org.testifj.matchers.core.BooleanThatIs;

public class BooleanExpectations extends ExpectationsBase<Boolean, BooleanExpectations> {

    public BooleanExpectations(ExpectationContext<Boolean> context) {
        super(context);
    }

    public void toBeTrue() {
        reportValueExpectation(BooleanThatIs.equalToTrue());
    }

    public void toBeFalse() {
        reportValueExpectation(BooleanThatIs.equalToFalse());
    }

    @Override
    protected BooleanExpectations newInstance(ExpectationContext<Boolean> context) {
        return new BooleanExpectations(context);
    }
}
