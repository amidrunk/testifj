package org.testifj;

import org.junit.Ignore;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

@SuppressWarnings("unchecked")
@Ignore
public class SpecificationDescriptionTest extends Specification {
    {

        final Action action = mock(Action.class);

        describe("constructor", (it) -> {
            it.should("not accept null target name", () -> {
                expect(() -> new SpecificationDescription(null, action)).toThrow(AssertionError.class);
            });

            it.should("not accept null specifier", () -> {
                expect(() -> new SpecificationDescription("foo", null)).toThrow(AssertionError.class);
            });

            it.should("retain arguments", () -> {
                final SpecificationDescription description = new SpecificationDescription("foo", action);

                expect(description.getTargetName()).to(equal("foo"));
                expect(description.getAction()).to(equal(action));
            });
        });

    }}
