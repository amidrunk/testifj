package org.testifj.delegate;

import org.testifj.Description;

public interface ExpectationDelegateExtension<T extends Expectation> {

    ExpectationVerification verify(ExpectationVerificationContext<T> expectation);

    Description describe(ExpectationVerificationContext<T> expectation);

}
