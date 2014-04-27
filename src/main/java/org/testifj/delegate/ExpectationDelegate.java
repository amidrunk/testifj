package org.testifj.delegate;

public interface ExpectationDelegate {

    OnGoingExpectation startExpectation();

    // OnGoingExpectation resume(OnGoingExpectation expectation) // Needed for chained actions

}
