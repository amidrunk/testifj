package org.testifj.matchers.core;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringThatIs.alphaNumeric;
import static org.testifj.matchers.core.StringThatIs.atLeastOfLength;

public class StringThatIsTest {

    @Test
    public void stringContainingShouldNotAcceptNullOrEmptyString() {
        expect(() -> StringThatIs.stringContaining(null)).toThrow(AssertionError.class);
        expect(() -> StringThatIs.stringContaining("")).toThrow(AssertionError.class);
    }

    @Test
    public void stringContainingShouldNotMatchStringWithoutSubString() {
        expect(StringThatIs.stringContaining("foo").matches("bar")).not().toBe(equalTo(true));
        expect(StringThatIs.stringContaining("foo").matches("")).not().toBe(equalTo(true));
    }

    @Test
    public void stringContainingShouldMatchStringWithSubString() {
        expect(StringThatIs.stringContaining("foo").matches("foo")).toBe(equalTo(true));
        expect(StringThatIs.stringContaining("foo").matches("foobar")).toBe(equalTo(true));
        expect(StringThatIs.stringContaining("foo").matches("xxxfoobar")).toBe(equalTo(true));
        assertFalse(StringThatIs.stringContaining("foo").matches(""));
        assertFalse(StringThatIs.stringContaining("foo").matches(null));
    }

    @Test
    public void emptyPredicateShouldOnlyMatchEmptyString() {
        assertFalse(StringThatIs.empty().matches(null));
        assertTrue(StringThatIs.empty().matches(""));
        assertFalse(StringThatIs.empty().matches("foo"));
    }

    @Test
    public void notEmptyShouldOnlyMatchNonEmptyString() {
        assertTrue(StringThatIs.notEmpty().matches("foo"));
        assertFalse(StringThatIs.notEmpty().matches(""));
        assertFalse(StringThatIs.notEmpty().matches(null));
    }

    @Test
    public void atLeastOfLengthShouldOnlyMatchStringWithMatchingMinimumLength() {
        assertTrue(atLeastOfLength(2).matches("foo"));
        assertTrue(atLeastOfLength(3).matches("foo"));
        assertFalse(atLeastOfLength(4).matches("foo"));
        assertFalse(atLeastOfLength(4).matches(""));
        assertFalse(atLeastOfLength(4).matches(null));
    }

    @Test
    public void atMostOfLengthShouldOnlyMatchStringWithMatchingMaximumLength() {
        assertTrue(StringThatIs.atMostOfLength(4).matches("foo"));
        assertTrue(StringThatIs.atMostOfLength(3).matches("foo"));
        assertFalse(StringThatIs.atMostOfLength(2).matches("foo"));
        assertTrue(StringThatIs.atMostOfLength(2).matches(""));
        assertFalse(StringThatIs.atMostOfLength(2).matches(null));
    }

    @Test
    public void ofLengthLessThanShouldOnlyMatchStringWithMaximumLengthExclusive() {
        assertTrue(StringThatIs.ofLengthLessThan(4).matches("foo"));
        assertFalse(StringThatIs.ofLengthLessThan(3).matches("foo"));
        assertTrue(StringThatIs.ofLengthLessThan(3).matches(""));
        assertFalse(StringThatIs.ofLengthLessThan(3).matches(null));
    }

    @Test
    public void ofLengthGreaterThanShouldOnlyMatchStringWithMinimumLengthInclusive() {
        assertTrue(StringThatIs.ofLengthGreaterThan(2).matches("foo"));
        assertFalse(StringThatIs.ofLengthGreaterThan(3).matches("foo"));
        assertFalse(StringThatIs.ofLengthGreaterThan(3).matches(null));
        assertFalse(StringThatIs.ofLengthGreaterThan(3).matches(""));
    }

    @Test
    public void ofPatternShouldOnlyMatchStringsMatchingPattern() {
        assertTrue(StringThatIs.ofPattern("[0-9]+").matches("1234"));
        assertFalse(StringThatIs.ofPattern("[0-9]+").matches("a1234"));
        assertFalse(StringThatIs.ofPattern("[0-9]+").matches("1234b"));
        assertFalse(StringThatIs.ofPattern("[0-9]+").matches(""));
        assertFalse(StringThatIs.ofPattern("[0-9]+").matches(null));
    }

    @Test
    public void upperCaseShouldOnlyMatchUpperCaseStrings() {
        assertTrue(StringThatIs.upperCase().matches("ABC"));
        assertTrue(StringThatIs.upperCase().matches("ABC123"));
        assertFalse(StringThatIs.upperCase().matches("ABCa"));
        assertFalse(StringThatIs.upperCase().matches(""));
        assertFalse(StringThatIs.upperCase().matches(null));
    }

    @Test
    public void lowerCaseShouldOnlyMatchLowerCaseStrings() {
        assertTrue(StringThatIs.lowerCase().matches("abc"));
        assertTrue(StringThatIs.lowerCase().matches("abc123"));
        assertFalse(StringThatIs.lowerCase().matches("Abc123"));
        assertFalse(StringThatIs.lowerCase().matches(""));
        assertFalse(StringThatIs.lowerCase().matches(null));
    }

    @Test
    public void alphaNumericShouldOnlyMatchStringsContainingOnlyAlphaNumericCharacters() {
        assertTrue(alphaNumeric().matches("abc123"));
        assertTrue(alphaNumeric().matches("Abc123"));
        assertTrue(alphaNumeric().matches("ABC123"));
        assertFalse(alphaNumeric().matches("ABC-123"));
        assertFalse(alphaNumeric().matches("ABC+123"));
        assertFalse(alphaNumeric().matches("ABC123?"));
        assertFalse(alphaNumeric().matches("ABC_123"));
        assertFalse(alphaNumeric().matches(""));
        assertFalse(alphaNumeric().matches(null));
    }

    @Test
    public void conditionsCanBeChained() {
        assertTrue(atLeastOfLength(3).and(alphaNumeric()).matches("foo"));
        assertTrue(atLeastOfLength(3).and(alphaNumeric()).matches("foobar"));
        assertFalse(atLeastOfLength(3).and(alphaNumeric()).matches("fo_"));
        assertFalse(atLeastOfLength(3).and(alphaNumeric()).matches("fo__"));
        assertFalse(atLeastOfLength(3).and(alphaNumeric()).matches(null));
        assertFalse(atLeastOfLength(3).and(alphaNumeric()).matches(""));
    }
}