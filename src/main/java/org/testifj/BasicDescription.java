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
    public Description appendText(String text) {
        assert text != null : "Text can't be null";

        if (text.isEmpty()) {
            return this;
        }

        return new BasicDescription(parts, new TextPartImpl(text));
    }

    @Override
    public Description appendDescription(Description description) {
        assert description != null : "Description can't be null";
        return new BasicDescription(parts, new DescriptionPartImpl(description));
    }

    @Override
    public Description appendValue(Object value) {
        return new BasicDescription(parts, new ValuePartImpl(value));
    }

    @Override
    public List<Part> getParts() {
        return Collections.unmodifiableList(parts);
    }

    public static Description from(String text) {
        assert text != null : "Text can't be null";
        return new BasicDescription().appendText(text);
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

    private static final class ValuePartImpl implements ValuePart {

        private final Object value;

        public ValuePartImpl(Object value) {
            this.value = value;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ValuePartImpl valuePart = (ValuePartImpl) o;

            if (value != null ? !value.equals(valuePart.value) : valuePart.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    private static final class DescriptionPartImpl implements DescriptionPart {

        private final Description description;

        private DescriptionPartImpl(Description description) {
            this.description = description;
        }

        @Override
        public Description getDescription() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DescriptionPartImpl that = (DescriptionPartImpl) o;

            if (!description.equals(that.description)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return description.hashCode();
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }

}
