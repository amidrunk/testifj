package org.testifj;

import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.ObjectThatIs.instanceOf;
import static org.testifj.matchers.core.ObjectThatIs.sameAs;

public class BasicDescriptionTest {

    private final BasicDescription emptyDescription = new BasicDescription();

    @Test
    public void builderCanCreateEmptyDescription() {
        expect(emptyDescription.getParts().toArray()).toBe(new Object[0]);
    }

    @Test
    public void returnedNodesCannotBeModified() {
        expect(() -> emptyDescription.getParts().add(mock(Description.Part.class)))
                .toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void toStringValueOfEmptyDescriptionShouldBeEmpty() {
        expect(emptyDescription.toString()).toBe("");
    }

    @Test
    public void appendTextShouldNotAcceptNullText() {
        expect(() -> emptyDescription.appendText(null)).toThrow(AssertionError.class);
    }

    @Test
    public void appendTextShouldReturnSameInstanceForEmptyText() {
        expect(emptyDescription.appendText("")).toBe(sameAs(emptyDescription));
    }

    @Test
    public void appendTextShouldReturnNewInstanceWithTextPart() {
        final BasicDescription newDescription = emptyDescription.appendText("foo");
        final List<Description.Part> parts = newDescription.getParts();
        expect(parts.size()).toBe(1);

        final Description.Part part = parts.get(0);
        expect(part).toBe(instanceOf(Description.TextPart.class));

        final Description.TextPart textPart = (Description.TextPart) part;
        expect(textPart.getText()).toBe("foo");
        expect(newDescription.toString()).toBe("foo");
    }

    @Test
    public void descriptionShouldBeEqualToItSelf() {
        expect(emptyDescription).toBe(equalTo(emptyDescription));
    }

    @Test
    public void emptyDescriptionsShouldBeEqual() {
        expect(emptyDescription).toBe(equalTo(new BasicDescription()));
        expect(emptyDescription.hashCode()).toBe(equalTo(new BasicDescription().hashCode()));
    }

    @Test
    public void descriptionsWithEqualTextPartsShouldBeEqual() {
        final BasicDescription description1 = new BasicDescription().appendText("foo");
        final BasicDescription description2 = new BasicDescription().appendText("foo");

        expect(description1).toBe(description2);
        expect(description1.hashCode()).toBe(description2.hashCode());
    }

    @Test
    public void descriptionsWithDifferentTextPartsShouldNotBeEqual() {
        final BasicDescription description1 = new BasicDescription().appendText("foo");
        final BasicDescription description2 = new BasicDescription().appendText("bar");

        expect(description1).not().toBe(equalTo(description2));
        expect(description1.hashCode()).not().toBe(equalTo(description2.hashCode()));
    }

    @Test
    public void descriptionsWithEqualPartSetsShouldNotBeEqual() {
        final BasicDescription description1 = new BasicDescription().appendText("foo");

        expect(description1).not().toBe(equalTo(emptyDescription));
        expect(description1.hashCode()).not().toBe(equalTo(emptyDescription.hashCode()));
    }

}
