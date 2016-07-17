package org.testifj;

import org.testifj.framework.BehavioralOutcome;
import org.testifj.framework.BehaviouralExpectation;
import org.testifj.framework.ExceptionalBehaviourCriterion;
import org.testifj.framework.ExpectationContext;
import org.testifj.matchers.core.ObjectThatIs;

public class BehaviouralExpectations extends ExpectationsBase<Procedure, BehaviouralExpectations> {

    public BehaviouralExpectations(ExpectationContext<Procedure> context) {
        super(context);
    }

    public<T extends Throwable> void toThrow(Class<T> type) { // Should return ThrowableExpectations (toThrow().whereMessageIs(stringContaining("Something"))
        BehavioralOutcome<?> outcome;

        try {
            getExpectationContext().getSubject().call();

            outcome = BehavioralOutcome.successful();
        } catch (Exception e) {
            outcome = BehavioralOutcome.exceptional(e);
        }

        getTestContext().expect(BehaviouralExpectation.builder()
                .outcome(outcome)
                .criterion(new ExceptionalBehaviourCriterion(ObjectThatIs.instanceOf(type)))
                .subject(getExpectationContext().getSubject())
                .expectationReference(getExpectationContext().getExpectationReference())
                .build());
    }

    public<T extends Throwable> void toThrow(Matcher<T> matcher) {

    }

    @Override
    protected BehaviouralExpectations newInstance(ExpectationContext<Procedure> context) {
        return new BehaviouralExpectations(context);
    }
}
