package org.testifj.integrationtest.codegen;

import org.junit.Test;
import io.recode.Caller;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.testifj.Expect.expect;

public class LambdaRegenerationTest extends TestOnDefaultConfiguration {

    @Test
    public void instanceMethodReferenceCanBeRegenerated() {
        acceptSupplier("foo"::length);

        expect(regenerate(Caller.adjacent(-2))).toBe("acceptSupplier(\"foo\"::length)");
    }

    @Test
    public void classMethodReferenceCanBeRegenerated() {
        acceptFunction(String::length);

        expect(regenerate(Caller.adjacent(-2))).toBe("acceptFunction(String::length)");
    }

    @Test
    public void inlineLambdaCanBeRegenerated() {
        acceptFunction(str -> str.length());

        expect(regenerate(Caller.adjacent(-2))).toBe("acceptFunction(str -> str.length())");
    }

    private void acceptSupplier(Supplier supplier) {}

    private void acceptFunction(Function<String, Integer> function) {}

}
