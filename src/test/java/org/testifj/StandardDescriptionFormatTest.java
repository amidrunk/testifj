package org.testifj;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class StandardDescriptionFormatTest {

    private final StandardDescriptionFormat format = new StandardDescriptionFormat();

    @Test
    public void formatShouldNotAcceptNullDescription() {
        expect(() -> format.format(null)).toThrow(AssertionError.class);
    }

    @Test
    public void textShouldBeFormattedAsPlainString() {
        final String description = format.format(BasicDescription.from("foo"));

        expect(description).toBe(description);
    }

    @Test
    public void stringShouldBeFormattedAsJavaString() {
        final String description = format.format(new BasicDescription().appendValue("foo"));

        expect(description).toBe("\"foo\"");
    }

    @Test
    public void nullValueShouldBeFormattedAsJavaNull() {
        final String description = format.format(new BasicDescription().appendValue(null));

        expect(description).toBe("null");
    }

    @Test
    public void arrayShouldBeFormattedWithValues() {
        final String description = format.format(new BasicDescription().appendValue(new Object[]{"foo", 1, true, null}));

        expect(description).toBe("[\"foo\", 1, true, null]");
    }

    @Test
    public void subDescriptionShouldBeFormatted() {
        final String description = format.format(new BasicDescription()
                .appendText("Sub description: ")
                .appendDescription(BasicDescription.from("foo")));

        expect(description).toBe("Sub description: foo");
    }

}
