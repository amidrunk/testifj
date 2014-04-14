package org.testifj;

public final class DefaultExpectationFailureHandler implements ExpectationFailureHandler {
    @Override
    public void handleExpectationFailure(ExpectationFailure failure) {
        if (failure instanceof ValueMismatchFailure) {
            final ValueMismatchFailure valueMismatchFailure = (ValueMismatchFailure) failure;

            if (valueMismatchFailure.getExpectedValue().isPresent()) {
                throw new AssertionError("Expected \"" + valueMismatchFailure.getExpectedValue().get() + "\", was: \"" + valueMismatchFailure.getValue() + "\"");
            }

            throw new AssertionError("Was: '" + valueMismatchFailure.getValue() + "'");
        }
    }
}
