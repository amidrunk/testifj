package org.testifj.integrationtest;

import org.junit.Ignore;
import org.junit.Test;
import io.recode.Caller;

import static org.testifj.Expect.expect;

@Ignore("Refactoring of code generation needs to be finalized")
public class PrimitiveBoxingTest extends TestOnDefaultConfiguration {

    @Test
    public void integerBoxingAndUnBoxingCanBeReGenerated() {
        int n = Integer.valueOf(1234);

        expect(regenerate(Caller.adjacent(-2))).toBe("int n = 1234");
    }

}
