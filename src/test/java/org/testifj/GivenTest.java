package org.testifj;

import org.junit.Test;

import static org.testifj.Given.given;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GivenTest {

    @Test
    public void specificationShouldBeCompletedIfFulfilled() {
        try {
            given(new StringBuilder()).
            when((s) -> s.append("hello")).
            then((s) -> s.toString().equals("hello"));
        } catch (Throwable e) {
            fail("Successful specification should not cause exception: " + e);
        }
    }

    @Test
    public void specificationShouldFailOnImplementationMismatch() {
        boolean failed = false;

        try {
            given(new StringBuilder()).
            when((s) -> s.append("foo")).
            then((s) -> s.toString().equals("bar"));
        } catch (AssertionError e) {
            failed = true;
            e.printStackTrace();
        }

        assertTrue("Specification should fail for implementation mismatch", failed);
    }

}
