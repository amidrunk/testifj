package org.testifj.framework;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class ValueDescriber implements Describer {

    private final boolean primitiveSuffix = true;

    @Override
    public Optional<String> describe(Object value) {
        if (value == null) {
            return Optional.of("null");
        }

        if (value instanceof String) {
            return Optional.of("\"" + value + "\"");
        }

        if (value instanceof Character) {
            return Optional.of("'" + value + "'");
        }

        if (primitiveSuffix) {
            if (value instanceof Long) {
                return Optional.of(value + "L");
            }

            if (value instanceof Float) {
                return Optional.of(value + "f");
            }

            if (value instanceof Double) {
                return Optional.of(value + "d");
            }
        }

        if (value instanceof Collection) {
            final String result = "[" + ((Collection) value).stream().map(v -> describe(v).get()).collect(joining(", ")) + "]";

            return Optional.of(result);
        }

        if (value.getClass().isArray()) {
            final StringBuilder buffer = new StringBuilder();
            final int length = Array.getLength(value);

            buffer.append("[");

            for (int i = 0; i < length; i++) {
                buffer.append(describe(Array.get(value, i)).get());

                if (i != length - 1) {
                    buffer.append(", ");
                }
            }

            return Optional.of(buffer.append("]").toString());
        }

        if (value instanceof Map) {
            final Iterator iterator = ((Map) value).entrySet().iterator();
            final StringBuilder buffer = new StringBuilder();

            buffer.append("{");

            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry) iterator.next();

                buffer.append(describe(entry.getKey()).get()).append(":").append(describe(entry.getValue()).get());

                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }

            return Optional.of(buffer.append("}").toString());
        }

        if (value instanceof Map.Entry) {
            final Map.Entry entry = (Map.Entry) value;

            return Optional.of(describe(entry.getKey()).get() + ":" + describe(entry.getValue()).get());
        }

        return Optional.of(String.valueOf(value));
    }
}
