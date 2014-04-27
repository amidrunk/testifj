package org.testifj.delegate;

public interface OnGoingExpectation {

    ExpectationVerification complete(Expectation expectation);

}
