package org.testifj.integrationtest.codegen;

import org.junit.Ignore;
import org.junit.Test;
import org.testifj.Issue;

import static org.testifj.Expect.expect;

@Ignore("Fix when generation of messages has been improved")
public class ExceptionSpecificationTest extends TestOnDefaultConfiguration {

    @Test
    @Issue(22)
    public void missingExpectedExceptionTypeShouldGenerateDescriptiveMessage() {
        final String message = messageOfFailure(() -> {
            expect(() -> System.currentTimeMillis()).toThrow(IllegalArgumentException.class);
        });

        expect(message).toBe("Expected [System.currentTimeMillis()] to throw IllegalArgumentException");
    }

}
