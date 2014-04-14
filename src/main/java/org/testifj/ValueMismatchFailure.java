package org.testifj;

import java.util.Optional;

public interface ValueMismatchFailure extends ExpectationFailure {

    Optional<Object> getExpectedValue();

    Object getValue();

    Matcher<?> getMatcher();

}
