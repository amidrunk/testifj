package org.testifj.integrationtest;

import org.junit.Test;
import org.testifj.matchers.core.ObjectThatIs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testifj.Expectations.expect;

public class ListExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void listCanBeExpectedToHaveSize() {
        final List<String> list = Arrays.asList("foo", "bar");

        expectNoException(() -> expect(list).toHaveSize(2));
        expectAssertionError(() -> expect(list).toHaveSize(0), "expected [list] => [\"foo\", \"bar\"] to have size 0");
        expectAssertionError(() -> expect(list).toHaveSize(1), "expected [list] => [\"foo\", \"bar\"] to have size 1");
        expectAssertionError(() -> expect(list).toHaveSize(3), "expected [list] => [\"foo\", \"bar\"] to have size 3");
    }

    @Test
    public void listCanBeExpectedToBeEmpty() {
        expectNoException(() -> expect(Collections.emptyList()).toBeEmpty());
        expectAssertionError(() -> expect(Arrays.asList("foo")).toBeEmpty(), "expected [Arrays.asList(\"foo\")] => [\"foo\"] to be empty");
    }

    @Test
    public void listCanBeExpectedNotToBeEmpty() {
        expectNoException(() -> expect(Arrays.asList("foo", "bar")).not().toBeEmpty());
        expectAssertionError(() -> expect(Collections.emptyList()).not().toBeEmpty(), "expected [Collections.emptyList()] => [] not to be empty");
    }

    @Test
    public void listCanBeExpectedToContainElementsMatchingCondition() {
        expect(Arrays.asList("foo", "bar")).toContain(
                ObjectThatIs.equalTo("foo"),
                ObjectThatIs.equalTo("bar")
        );

        expectAssertionError(() -> expect(Arrays.asList("foo", "bar")).toContain(
                ObjectThatIs.equalTo("foo"),
                ObjectThatIs.equalTo("BAR")
        ), "expected [Arrays.asList(\"foo\", \"bar\")] => [\"foo\", \"bar\"] to contain");
    }

    @Test
    public void listCanBeExpectedToContainExactlyElements() {
        expect(Arrays.asList("foo", "bar")).toContainExactly(
                ObjectThatIs.equalTo("foo"),
                ObjectThatIs.equalTo("bar")
        );

        expectAssertionError(() -> expect(Arrays.asList("foo", "bar")).toContainExactly(
                ObjectThatIs.equalTo("bar"),
                ObjectThatIs.equalTo("foo")
        ), "expected [Arrays.asList(\"foo\", \"bar\")] => [\"foo\", \"bar\"] to contain exactly");
    }

    @Test
    public void listCanBeExpectedToContainElements() {
        expectNoException(() -> expect(Arrays.asList("foo", "bar")).toContainExactly("foo", "bar"));
        expectAssertionError(() -> expect(Arrays.asList("foo", "bar")).toContainExactly("foo"),
                "expected [Arrays.asList(\"foo\", \"bar\")] => [\"foo\", \"bar\"] to contain exactly \"foo\"");
        expectAssertionError(() -> expect(Arrays.asList("foo", "bar")).toContainExactly("bar", "foo"),
                "expected [Arrays.asList(\"foo\", \"bar\")] => [\"foo\", \"bar\"] to contain exactly \"bar\", \"foo\"");
    }
}
