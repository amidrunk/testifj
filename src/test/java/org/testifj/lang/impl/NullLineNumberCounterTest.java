package org.testifj.lang.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class NullLineNumberCounterTest {

    @Test
    public void getShouldAlwaysReturnNegative() {
        given(new NullLineNumberCounter()).then(it -> {
            expect(it.get()).toBe(-1);
        });
    }

}
