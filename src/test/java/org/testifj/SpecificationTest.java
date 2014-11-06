package org.testifj;

import org.junit.Ignore;
import org.junit.Test;
import org.testifj.matchers.core.CollectionThatIs;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;

@Ignore
@SuppressWarnings("unchecked")
public class SpecificationTest {

    private final Specification specification = new Specification() {};

    @Test
    public void describeShouldNotAcceptInvalidArguments() {
        expect(() -> specification.describe(null, mock(Action.class))).toThrow(AssertionError.class);
        expect(() -> specification.describe("", mock(Action.class))).toThrow(AssertionError.class);
        expect(() -> specification.describe("foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void descriptionsAndActionsShouldBeRetained() {
        final Action action = mock(Action.class);

        specification.describe("foo", action);

        expect(specification.getSpecificationDescriptions()).toBe(collectionOf(new SpecificationDescription("foo", action)));
    }

}
