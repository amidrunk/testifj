package org.testifj.lang.impl;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class LineNumberTableEntryImplTest {

    @Test
    public void constructorShouldRetainParameters() {
        final LineNumberTableEntryImpl entry = new LineNumberTableEntryImpl(1, 2);

        expect(entry.getStartPC()).toBe(1);
        expect(entry.getLineNumber()).toBe(2);
    }

}
