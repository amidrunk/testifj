package org.testifj;

import org.junit.Ignore;
import org.junit.Test;
import org.testifj.matchers.core.StringThatIs;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringThatIs.stringContaining;

@SuppressWarnings("unchecked")
public class SpecificationDescriptionTest {

    @Test
    public void targetNameCannotBeNullOrEmpty() {
        expect(() -> new SpecificationDescription(null, mock(Action.class))).toThrow(AssertionError.class);
        expect(() -> new SpecificationDescription("", mock(Action.class))).toThrow(AssertionError.class);
    }

    @Test
    public void actionCannotBeNull() {
        expect(() -> new SpecificationDescription("foo", null)).toThrow(AssertionError.class);
    }

    @Test
    public void targetNameAndActionShouldBeDefined() {
        final Action action = mock(Action.class);

        given(new SpecificationDescription("foo", action)).then(it -> {
            expect(it.getTargetName()).toBe("foo");
            expect(it.getAction()).toBe(action);
        });
    }

    @Test
    public void instanceShouldBeEqualToItself() {
        final SpecificationDescription description = new SpecificationDescription("foo", mock(Action.class));

        expect(description).toBe(equalTo(description));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        given(new SpecificationDescription("foo", mock(Action.class))).then(it -> {
            expect(it).not().toBe(equalTo(null));
            expect((Object) it).not().toBe(equalTo("foo"));
        });
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Action action = mock(Action.class);
        final SpecificationDescription description1 = new SpecificationDescription("foo", action);
        final SpecificationDescription description2 = new SpecificationDescription("foo", action);

        expect(description1).toBe(equalTo(description2));
        expect(description1.hashCode()).toBe(equalTo(description2.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final Action action = mock(Action.class);
        final SpecificationDescription description = new SpecificationDescription("foo", action);

        expect(description.toString()).toBe(stringContaining("foo"));
        expect(description.toString()).toBe(stringContaining(action.toString()));
    }

}
