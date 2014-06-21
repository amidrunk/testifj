package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.impl.LineNumberTableEntryImpl;

import static org.testifj.Expect.expect;

public class LineNumberTableEntryImplTest {

    @Test
    public void constructorShouldRetainParameters() {
        final LineNumberTableEntryImpl entry = new LineNumberTableEntryImpl(1, 2);

        expect(entry.getStartPC()).toBe(1);
        expect(entry.getLineNumber()).toBe(2);
    }

}
