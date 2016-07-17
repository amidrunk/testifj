package org.testifj;

import org.testifj.framework.*;
import org.testifj.matchers.core.ObjectThatIs;

public class ObjectExpectations<T> extends ExpectationsBase<T, ObjectExpectations<T>> {

    public ObjectExpectations(ExpectationContext<T> context) {
        super(context);
    }

    public void toBeNull() {
        reportValueExpectation(ObjectThatIs.equalTo(null));
    }

    public void toEqual(T anotherObject) {
        reportValueExpectation(ObjectThatIs.equalTo(anotherObject));
    }

    @Override
    protected ObjectExpectations<T> newInstance(ExpectationContext<T> context) {
        return new ObjectExpectations<T>(context);
    }
}
