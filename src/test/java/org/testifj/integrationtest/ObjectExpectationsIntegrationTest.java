package org.testifj.integrationtest;

import org.junit.Test;

import static org.testifj.Expectations.expect;

public class ObjectExpectationsIntegrationTest extends AbstractIntegrationTestBase {

    @Test
    public void itShouldBePossibleToExpectAnObjectNotToBeNull() {
        expectNoException(() -> expect("anObject").not().toBeNull());
        expectAssertionError(() -> expect((Object) null).not().toBeNull(), "expected [null] not to be null");
    }

    @Test
    public void objectCanBeExpectedToBeTheSameAsAnotherObject() {
        final String str1 = new String("foo");
        final String str2 = new String("foo");

        expectNoException(() -> expect(str1).toBeTheSameAs(str1));
        expectAssertionError(() -> expect(str1).toBeTheSameAs(str2), "expected [str1] => \"foo\" to be the same as [str2] => \"foo\"");
    }
}
