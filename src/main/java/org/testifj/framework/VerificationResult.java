package org.testifj.framework;

import java.util.List;

public interface VerificationResult {

    Criterion getCriterion();

    VerificationOutcome getOutcome();

    List<VerificationResult> getSubVerificationResults();
}
