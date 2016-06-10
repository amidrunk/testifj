package org.testifj;

import org.junit.Test;

import static org.testifj.Expectations.expect;

public class ObjectExpectationsTest {



    @Test
    public void itShouldBePossibleToExpectAnObjectToBeNull() {
        expect(new Object()).toBeNull();
    }

}
