package org.testifj.matchers.core;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapThatIsTest {

    @Test
    public void emptyMatcherShouldOnlyMatchEmptyMap() {
        final Map<String, String> nonEmptyMap = new HashMap<>();

        nonEmptyMap.put("foo", "bar");

        assertTrue(MapThatIs.empty().matches(Collections.emptyMap()));
        assertFalse(MapThatIs.<String, String>empty().matches(nonEmptyMap));
    }

    @Test
    public void ofSizeMatcherShouldMatchMapWithCorrectSize() {
        final Map<String, String> map = new HashMap<>();

        map.put("foo", "bar");

        assertFalse(MapThatIs.<String, String>ofSize(0).matches(map));
        assertTrue(MapThatIs.<String, String>ofSize(1).matches(map));
        assertFalse(MapThatIs.<String, String>ofSize(0).matches(map));
    }

    @Test
    public void containingMatcherShouldMatchMapContainingKeyValue() {
        final Map<String, String> map = new HashMap<>();

        map.put("foo", "bar");

        assertFalse(MapThatIs.containing("bar", "baz").matches(map));
        assertTrue(MapThatIs.containing("foo", "bar").matches(map));
    }
}