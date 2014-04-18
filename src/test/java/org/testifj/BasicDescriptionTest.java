package org.testifj;

import org.junit.Test;
import org.testifj.matchers.core.CollectionThatIs;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.*;

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
        final Description newDescription = emptyDescription.appendText("foo");
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
        final Description description1 = new BasicDescription().appendText("foo");
        final Description description2 = new BasicDescription().appendText("foo");

        expect(description1).toBe(description2);
        expect(description1.hashCode()).toBe(description2.hashCode());
    }

    @Test
    public void descriptionsWithDifferentTextPartsShouldNotBeEqual() {
        final Description description1 = new BasicDescription().appendText("foo");
        final Description description2 = new BasicDescription().appendText("bar");

        expect(description1).not().toBe(equalTo(description2));
        expect(description1.hashCode()).not().toBe(equalTo(description2.hashCode()));
    }

    @Test
    public void descriptionsWithEqualPartSetsShouldNotBeEqual() {
        final Description description1 = new BasicDescription().appendText("foo");

        expect(description1).not().toBe(equalTo(emptyDescription));
        expect(description1.hashCode()).not().toBe(equalTo(emptyDescription.hashCode()));
    }

    @Test
    public void fromShouldNotAcceptNullText() {
        expect(() -> BasicDescription.from(null)).toThrow(AssertionError.class);
    }

    @Test
    public void fromShouldReturnDescriptionWithTextContents() {
        expect(BasicDescription.from("foo")).toBe(new BasicDescription().appendText("foo"));
    }

    @Test
    public void fromEmptyStringShouldYieldEmptyDescription() {
        expect(BasicDescription.from("").getParts()).toBe(CollectionThatIs.empty());
    }

    @Test
    public void appendValueShouldReturnDescriptionWithValueParty() {
        final Description description = new BasicDescription().appendValue("foo");

        expect(description.getParts().size()).toBe(1);
        expect(description.getParts().get(0)).toBe(instanceOf(Description.ValuePart.class));

        final Description.ValuePart valuePart = (Description.ValuePart) description.getParts().get(0);

        expect(valuePart.getValue()).toBe("foo");
    }

    @Test
    public void appendDescriptionShouldNotAcceptNullDescription() {
        expect(() -> emptyDescription.appendDescription(null)).toThrow(AssertionError.class);
    }

    @Test
    public void appendDescriptionShouldCreateDescriptionWithSubDescription() {
        final Description subDescription = new BasicDescription().appendText("Hello!");
        final Description outerDescription = emptyDescription.appendDescription(subDescription);

        expect(outerDescription.getParts().size()).toBe(1);
        expect(outerDescription.getParts().get(0)).toBe(instanceOf(Description.DescriptionPart.class));

        final Description.DescriptionPart subDescriptionPart = (Description.DescriptionPart) outerDescription.getParts().get(0);
        expect(subDescriptionPart.getDescription()).toBe(subDescription);
    }

    @Test
    public void descriptionsWithEqualSubDescriptionsShouldBeEqual() {
        final Description description1 = new BasicDescription().appendDescription(BasicDescription.from("foo"));
        final Description description2 = new BasicDescription().appendDescription(BasicDescription.from("foo"));

        expect(description1).toBe(equalTo(description2));
        expect(description1.hashCode()).toBe(equalTo(description2.hashCode()));
    }

    @Test
    public void descriptionsWithUnEqualSubDescriptionsShouldBeUnEqual() {
        final Description description1 = new BasicDescription().appendDescription(BasicDescription.from("foo"));
        final Description description2 = new BasicDescription().appendDescription(BasicDescription.from("bar"));

        expect(description1).not().toBe(equalTo(description2));
        expect(description1.hashCode()).not().toBe(equalTo(description2.hashCode()));
    }

}

