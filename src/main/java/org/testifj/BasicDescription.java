package org.testifj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BasicDescription implements Description {

    private final List<Part> parts;

    public BasicDescription() {
        this(Collections.emptyList());
    }

    private BasicDescription(List<Part> parts) {
        this.parts = parts;
    }

    private BasicDescription(List<Part> parts, Part newPart) {
        this.parts = new ArrayList<>(parts.size() + 1);
        this.parts.addAll(parts);
        this.parts.add(newPart);
    }

    @Override
    public BasicDescription appendText(String text) {
        assert text != null : "Text can't be null";

        if (text.isEmpty()) {
            return this;
        }

        return new BasicDescription(parts, new TextPartImpl(text));
    }

    @Override
    public List<Part> getParts() {
        return Collections.unmodifiableList(parts);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        for (Part part : parts) {
            buffer.append(part.toString());
        }

        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicDescription that = (BasicDescription) o;

        if (!parts.equals(that.parts)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return parts.hashCode();
    }

    private static final class TextPartImpl implements TextPart {

        private final String text;

        private TextPartImpl(String text) {
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TextPartImpl textPart = (TextPartImpl) o;

            if (!text.equals(textPart.text)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return text.hashCode();
        }
    }
}
