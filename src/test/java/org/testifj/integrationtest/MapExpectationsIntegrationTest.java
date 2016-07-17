package org.testifj.integrationtest;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testifj.Expectations.expect;

public class MapExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void mapCanBeExpectedToBeEmpty() {
        final Map<String, Object> emptyMap = new HashMap<>();
        final Map<String, Object> nonEmptyMap = new HashMap<>();

        nonEmptyMap.put("foo", "bar");

        expectNoException(() -> expect(emptyMap).toBeEmpty());
        expectAssertionError(() -> expect(nonEmptyMap).toBeEmpty(), "expected [nonEmptyMap] => {\"foo\":\"bar\"} to be empty");
    }

    @Test
    public void mapCanBeExpectedNotToBeEmpty() {
        final Map<String, Object> emptyMap = new HashMap<>();
        final Map<String, Object> nonEmptyMap = new HashMap<>();

        nonEmptyMap.put("foo", "bar");

        expectAssertionError(() -> expect(emptyMap).not().toBeEmpty(), "expected [emptyMap] => {} not to be empty");
        expectNoException(() -> expect(nonEmptyMap).not().toBeEmpty());
    }

    @Test
    public void mapCanBeExpectedToHaveSize() {
        final Map<String, String> map = new HashMap<>();

        map.put("foo", "bar");

        expectNoException(() -> expect(map).toHaveSize(1));
        expectAssertionError(() -> expect(map).toHaveSize(0), "expected [map] => {\"foo\":\"bar\"} to have size 0");
        expectAssertionError(() -> expect(map).toHaveSize(2), "expected [map] => {\"foo\":\"bar\"} to have size 2");
    }

    @Test
    public void mapCanBeExpectedToContainEntry() {
        final Map<String, String> map = new HashMap<>();

        map.put("foo", "bar");

        expectNoException(() -> expect(map).toContainKey("foo").withValue("bar"));
        expectAssertionError(() -> expect(map).toContainKey("foo").withValue("baz"), "expected [map] => {\"foo\":\"bar\"} to contain key [\"foo\"] with value [\"baz\"]");
    }
}
