package org.testifj;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Describe.describe;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.StringShould.containString;

public class DescribeTest {

    @Test
    public void describeShouldNotAcceptInvalidArguments() {
        expect(() -> describe(null, mock(Procedure.class))).toThrow(AssertionError.class);
        expect(() -> describe("foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void describeShouldRunProcedureAndReturnIfSuccess() throws Exception {
        final Procedure procedure = mock(Procedure.class);

        describe("myProcedure", procedure);

        verify(procedure).call();
    }

    @Test
    public void describeShouldFailWithMessageIfProcedureFails() throws Exception {
        expect(() -> describe("myProcedure", () -> expect(1).toBe(2)))
                .toThrow(AssertionError.class)
                .withMessage(containString("myProcedure").and(containString("Expected 1 to be 2")));
    }

}