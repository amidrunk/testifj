package org.testifj;

import org.testifj.Action;
import org.testifj.Specification;

import static org.testifj.Expect.expect;
import static org.mockito.Mockito.mock;

public class SpecificationTest extends Specification {{

    final Specification specification = new Specification() { };

    describe("describe", (it) -> {
        it.should("not accept null target name", () -> {
            expect(() -> specification.describe(null, mock(Action.class))).
            toThrow(AssertionError.class);
        });

        it.should("not accept null action", () -> {
            expect(() -> specification.describe("foo", null)).
            toThrow(AssertionError.class);
        });
    });

}}
