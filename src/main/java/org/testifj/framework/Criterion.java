package org.testifj.framework;

public interface Criterion {

    VerificationResult verify(Expectation<?> expectation);
}
