package org.testifj.matchers.core;

import org.testifj.Matcher;

import java.util.Map;
import java.util.Objects;

public class MapThatIs {

    public static<K, V> Matcher<Map<K, V>> empty() {
        return map -> map != null && map.isEmpty();
    }

    public static<K, V> Matcher<Map<K, V>> ofSize(int size) {
        return map -> map != null && map.size() == size;
    }

    public static<K, V> Matcher<Map<K, V>> containing(K key, V value) {
        return map -> map != null && Objects.equals(value, map.get(key));
    }
}
