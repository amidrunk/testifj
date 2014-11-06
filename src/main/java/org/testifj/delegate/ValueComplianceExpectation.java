package org.testifj.delegate;

import org.testifj.Matcher;

import java.util.Optional;

/**
 * A <code>ValueComplianceExpectation</code> is created when an expectation is created from an
 * <code>expect(..).toBe(..)</code>-statement.
 */
public interface ValueComplianceExpectation extends Expectation {

    Object getActualValue();

    Optional<Object> getExpectedValue();

    Matcher getMatcher();

}
