package org.testifj.matchers.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class CollectionThatIsTest {

    @Test
    public void emptyShouldMatchEmptyCollection() {
        expect(CollectionThatIs.empty().matches(Collections.emptyList())).to(equal(true));
    }

    @Test
    public void emptyShouldNotMatchNonEmptyCollection() {
        expect(CollectionThatIs.empty().matches(Arrays.asList("foo"))).to(equal(false));
    }

    @Test
    public void emptyShouldNotMatchNull() {
        expect(CollectionThatIs.empty().matches(null)).to(equal(false));
    }

}
