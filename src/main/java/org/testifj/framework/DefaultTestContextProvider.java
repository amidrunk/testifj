package org.testifj.framework;

import io.recode.codegeneration.impl.CodePointerCodeGenerator;
import io.recode.decompile.CodeLocationDecompiler;
import io.recode.decompile.impl.CodeLocationDecompilerImpl;

import java.util.Optional;

public class DefaultTestContextProvider implements TestContextProvider {

    private final CodeLocationDecompiler decompiler = new CodeLocationDecompilerImpl();

    private final CodePointerCodeGenerator codePointerCodeGenerator = new CodePointerCodeGenerator();

    private final ExpectationDescriber expectationDescriber = new ExpectationDescriber(decompiler, codePointerCodeGenerator, new ValueDescriber());

    @Override
    public TestContext getTestContext() {
        return expectation -> {
            final VerificationResult result = expectation.getCriterion().verify(expectation);

            if (result.getOutcome() == VerificationOutcome.NOT_SUPPORTED) {
                throw new UnsupportedOperationException("The provided expectation could not be validated");
            }

            if (result.getOutcome() == VerificationOutcome.INVALID) {
                final Optional<String> description = expectationDescriber.describe(expectation);

                throw new AssertionError(description.orElse("expectation failed"));
            }
        };
    }
}
