package org.testifj;

import java.lang.reflect.Array;

public final class StandardDescriptionFormat implements DescriptionFormat {

    @Override
    public String format(Description description) {
        assert description != null : "Description can't be null";

        final StringBuilder buffer = new StringBuilder();

        appendDescription(description, buffer);

        return buffer.toString();
    }

    private void appendDescription(Description description, StringBuilder buffer) {
        description.getParts().forEach(part -> {
            if (part instanceof Description.TextPart) {
                final Description.TextPart textPart = (Description.TextPart) part;

                buffer.append(textPart.getText());
            } else if (part instanceof Description.ValuePart) {
                final Description.ValuePart valuePart = (Description.ValuePart) part;

                appendValue(valuePart.getValue(), buffer);
            } else if (part instanceof Description.DescriptionPart) {
                final Description.DescriptionPart descriptionPart = (Description.DescriptionPart) part;

                appendDescription(descriptionPart.getDescription(), buffer);
            }
        });
    }

    private void appendValue(Object value, StringBuilder buffer) {
        if (value == null) {
            buffer.append("null");
        } else if (value instanceof String) {
            buffer.append("\"").append(value).append("\"");
        } else if (value.getClass().isArray()) {
            final int length = Array.getLength(value);

            buffer.append("[");

            for (int i = 0; i < length; i++) {
                appendValue(Array.get(value, i), buffer);

                if (i != length - 1) {
                    buffer.append(", ");
                }
            }

            buffer.append("]");
        } else {
            buffer.append(String.valueOf(value));
        }
    }
}
