package org.testifj;

import org.junit.Ignore;

import static org.testifj.Expect.expect;

@Ignore
public class SpecificationRunnerTest extends Specification {
    {

        describe("constructor", it -> {
            it.should("not accept null test class", () -> {
                expect(() -> new SpecificationRunner(null)).toThrow(AssertionError.class);
            });

            it.should("not accept invalid test class type", () -> {
                expect(() -> new SpecificationRunner(String.class)).toThrow(AssertionError.class);
            });
        });

    }}
