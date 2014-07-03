package org.testifj.util;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class PairTest {

    private final Pair<String, String> examplePair = new Pair<>("foo", "bar");

    @Test
    public void constructorShouldRetainArguments() {
        given(new Pair<>("foo", "bar")).then(it -> {
            expect(it.left()).toBe("foo");
            expect(it.right()).toBe("bar");
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(examplePair).toBe(equalTo(examplePair));
    }

    @Test
    public void pairShouldNotBeEqualToNullOrDifferentType() {
        expect(examplePair).not().toBe(equalTo(null));
        expect((Object) examplePair).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Pair<String, String> other = new Pair<>("foo", "bar");

        expect(examplePair).toBe(equalTo(other));
        expect(examplePair.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainElements() {
        expect(examplePair.toString()).to(containString("foo"));
        expect(examplePair.toString()).to(containString("bar"));
    }

    @Test
    public void newPairWithChangedLeftElementCanBeCreated() {
        final Pair<String, String> pair = Pair.of("foo", "bar");
        expect(pair.left(1234)).toBe(Pair.of(1234, "bar"));
    }

    @Test
    public void newPairWithChangedRightElementCanBeCreated() {
        final Pair<String, String> pair = Pair.of("foo", "bar");
        expect(pair.right(1234)).toBe(Pair.of("foo", 1234));
    }

}