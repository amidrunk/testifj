package org.testifj.delegate;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.testifj.Action;

import java.util.Objects;

public final class ExpectationMatchers {

    public static Matcher<Expectation> isGivenThenWith(Class<?> sourceClass, String sourceMethod, Object actualValue, Action verificationAction) {
        return new BaseMatcher<Expectation>() {
            @Override
            public boolean matches(Object item) {
                if (!(item instanceof GivenThenExpectation)) {
                    return false;
                }

                final GivenThenExpectation expectation = (GivenThenExpectation) item;

                if (!expectation.getCaller().getClassName().equals(sourceClass.getName())) {
                    return false;
                }

                if (!expectation.getCaller().getMethodName().equals(sourceMethod)) {
                    return false;
                }

                if (!Objects.equals(actualValue, expectation.getProvidedValue())) {
                    return false;
                }

                if (!verificationAction.equals(expectation.getVerificationAction())) {
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("isGivenThenWith(" +
                        "sourceClass=" + sourceClass + ", " +
                        "sourceMethod=" + sourceMethod + ", " +
                        "actualValue = " + actualValue + ", " +
                        "verificationAction = " + verificationAction + ")");
            }
        };
    }

}
