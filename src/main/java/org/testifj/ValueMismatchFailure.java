package org.testifj;

public interface ValueMismatchFailure extends ExpectationFailure {

    Object getValue();

    Matcher<?> getMatcher();

}
