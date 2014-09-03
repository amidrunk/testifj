package org.testifj.matchers.core;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

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
    }

}