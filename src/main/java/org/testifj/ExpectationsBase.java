package org.testifj;

import org.testifj.framework.*;
import org.testifj.matchers.core.ObjectThatIs;

import java.util.Optional;

public abstract class ExpectationsBase<T, E extends ExpectationsBase<T, E>> {

    private final ExpectationContext<T> context;

    public ExpectationsBase(ExpectationContext<T> context) {
        this.context = context;
    }

    public E not() {
        return newInstance(getExpectationContext().invert());
    }

    public void toEqual(T other) {
        reportValueExpectation(ObjectThatIs.equalTo(other), Optional.of(other));
    }

    public void toBe(Matcher<T> matcher) {
        assert matcher != null : "matcher can't be null";
        reportValueExpectation(matcher);
    }

    public void toBeTheSameAs(T other) {
        reportValueExpectation(ObjectThatIs.sameAs(other), Optional.of(other));
    }

    protected ExpectationContext<T> getExpectationContext() {
        return context;
    }

    protected final TestContext getTestContext() {
        return TestContextProviders.configuredTestContextProvider().getTestContext();
    }

    protected Matcher<T> withContextDecoration(Matcher<T> matcher) {
        if (getExpectationContext().isInverted()) {
            return value -> !matcher.matches(value);
        }

        return matcher;
    }

    protected void reportValueExpectation(Matcher<T> matcher) {
        reportValueExpectation(matcher, Optional.empty());
    }

    protected void reportValueExpectation(Matcher<T> matcher, Optional<T> expectedValue) {
        final ValueExpectation.Builder builder = ValueExpectation.builder()
                .subject(getExpectationContext().getSubject())
                .expectationReference(getExpectationContext().getExpectationReference())
                .criterion(new MatchCriterion(withContextDecoration(matcher)));

        expectedValue.ifPresent(v -> builder.expectedValue(v));

        getTestContext().expect(builder.build());
    }

    protected abstract E newInstance(ExpectationContext<T> context);
}
