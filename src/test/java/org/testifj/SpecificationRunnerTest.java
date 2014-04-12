package org.testifj;

import static org.testifj.Expect.expect;

public class SpecificationRunnerTest extends Specification {{

    describe("constructor", it -> {
        it.should("not accept null test class", () -> {
            expect(() -> new SpecificationRunner(null)).toThrow(AssertionError.class);
        });

        it.should("not accept invalid test class type", () -> {
            expect(() -> new SpecificationRunner(String.class)).toThrow(AssertionError.class);
        });
    });

}}
