package org.testifj.framework;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValueDescriberTest {

    private final Describer describer = new ValueDescriber();

    @Test
    public void nullCanBeDescribed() {
        assertEquals(Optional.of("null"), describer.describe(null));
    }

    @Test
    public void stringCanBeDescribed() {
        assertEquals(Optional.of("\"foo\""), describer.describe("foo"));
    }

    @Test
    public void primitivesCanBeDescribed() {
        assertEquals(Optional.of("1"), describer.describe((byte) 1));
        assertEquals(Optional.of("1"), describer.describe((short) 1));
        assertEquals(Optional.of("1"), describer.describe((int) 1));
        assertEquals(Optional.of("1L"), describer.describe((long) 1));
        assertEquals(Optional.of("1.0f"), describer.describe((float) 1));
        assertEquals(Optional.of("1.0d"), describer.describe((double) 1));
        assertEquals(Optional.of("'x'"), describer.describe('x'));
        assertEquals(Optional.of("true"), describer.describe(true));
        assertEquals(Optional.of("false"), describer.describe(false));
    }

    @Test
    public void listCanBeDescribed() {
        assertEquals(Optional.of("[\"a\", 1, 2]"), describer.describe(Arrays.asList("a", 1, 2)));
    }

    @Test
    public void arrayCanBeDescribed(){
        assertEquals(Optional.of("[\"foo\", \"bar\"]"), describer.describe(new String[]{"foo", "bar"}));
    }

    @Test
    public void mapCanBeDescribed() {
        final Map<String, Integer> map = new LinkedHashMap<>();

        map.put("foo", 1);
        map.put("bar", 2);

        assertEquals(Optional.of("{\"foo\":1, \"bar\":2}"), describer.describe(map));
    }

    @Test
    public void mapWithStringValueCanBeDescribed() {
        final Map<String, String> map = new LinkedHashMap<>();

        map.put("key1", "value1");
        map.put("key2", "value2");

        assertEquals(Optional.of("{\"key1\":\"value1\", \"key2\":\"value2\"}"), describer.describe(map));
    }

    @Test
    public void mapEntryCanBeDescribed() {
        final Map.Entry entry = mock(Map.Entry.class);

        when(entry.getKey()).thenReturn("aKey");
        when(entry.getValue()).thenReturn("aValue");

        assertEquals(Optional.of("\"aKey\":\"aValue\""), describer.describe(entry));
    }
}