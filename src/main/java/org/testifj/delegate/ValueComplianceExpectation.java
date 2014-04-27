package org.testifj.delegate;

import java.util.Optional;

public interface ValueComplianceExpectation extends Expectation {

    Object getActualValue();

    Optional<Object> getExpectedValue();


}
