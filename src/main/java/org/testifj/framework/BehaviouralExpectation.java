package org.testifj.framework;

import org.testifj.Procedure;

public class BehaviouralExpectation implements Expectation<Procedure> {

    @Override
    public Procedure getSubject() {
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
}
