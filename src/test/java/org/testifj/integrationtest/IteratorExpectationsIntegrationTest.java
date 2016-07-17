package org.testifj.integrationtest;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.testifj.Expectations.expect;

public class IteratorExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void iteratorCanBeExpectedToHaveNextElement() {
        final Iterator<String> iterator = Arrays.asList("foo").iterator();

        expectNoException(() -> expect(iterator).toHaveNext());
        expectAssertionError(() -> expect(iterator).not().toHaveNext(), "expected [iterator] not to have next");

        iterator.next();

        expectAssertionError(() -> expect(iterator).toHaveNext(), "expected [iterator] to have next");
        expectNoException(() -> expect(iterator).not().toHaveNext());
    }

    @Test
    public void iteratorCanBeExpectedToContain() {
        expectNoException(() -> expect(Arrays.asList("foo", "bar").iterator()).toContain("foo", "bar"));
        expectAssertionError(() -> expect(Arrays.asList("foo", "bar").iterator()).toContain("bar", "foo"), "expected [Arrays.asList(\"foo\", \"bar\").iterator()] to contain \"bar\", \"foo\"");
        expectAssertionError(() -> expect(Arrays.asList("foo", "bar").iterator()).toContain("foo"), "");
        expectAssertionError(() -> expect(Arrays.asList("foo", "bar").iterator()).toContain("foo", "bar", "baz"), "");
    }
}
