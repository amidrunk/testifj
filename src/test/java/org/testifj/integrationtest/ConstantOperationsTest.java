package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.Caller;

import static org.testifj.Expect.expect;

public class ConstantOperationsTest extends TestOnDefaultConfiguration {

    @Test
    public void nullConstantCanBeRegenerated() {
        String str = null;

        expect(regenerate(Caller.adjacent(-2))).toBe("String str = null");
    }

}
