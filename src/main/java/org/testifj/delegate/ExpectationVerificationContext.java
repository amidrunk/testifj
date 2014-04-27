package org.testifj.delegate;

import org.testifj.ServiceContext;

import java.util.List;

public interface ExpectationVerificationContext<T extends Expectation> extends ServiceContext {

    T getExpectation();

    List<ExpectationVerificationContext> getSubContexts();

}
