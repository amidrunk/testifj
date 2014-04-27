package org.testifj.delegate;

import org.testifj.ServiceContext;

import java.util.Collections;

public final class DefaultExpectationDelegate implements ExpectationDelegate {

    private final ServiceContext serviceContext;

    private final ExpectationDelegateConfiguration configuration;

    public DefaultExpectationDelegate(ServiceContext serviceContext, ExpectationDelegateConfiguration configuration) {
        assert serviceContext != null : "Service context can't be null";
        assert configuration != null : "Configuration can't be null";

        this.serviceContext = serviceContext;
        this.configuration = configuration;
    }

    @Override
    public OnGoingExpectation startExpectation() {
        return new OnGoingExpectation() {
            @Override
            public ExpectationVerification complete(Expectation expectation) {
                final ExpectationVerificationContext<Expectation> expectationVerificationContext = new ExpectationVerificationContextImpl<>(expectation, Collections.emptyList(), serviceContext);
                final ExpectationDelegateExtension<Expectation> extension = configuration.getExtension(expectationVerificationContext);

                if (extension == null) {
                    throw new UnsupportedOperationException("Incomplete configuration, expectation not handled: " + expectation);
                }

                return extension.verify(expectationVerificationContext);
            }
        };
    }
}
