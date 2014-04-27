package org.testifj.lang.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.LineNumberTable;
import org.testifj.lang.LineNumberTableEntry;

import java.io.IOException;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class LineNumberTableImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new LineNumberTableImpl(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() throws IOException {
        final LineNumberTableEntry entry = Mockito.mock(LineNumberTableEntry.class);
        final LineNumberTableImpl table = new LineNumberTableImpl(new LineNumberTableEntry[]{entry});

        expect(table.getEntries().toArray()).toBe(new Object[]{entry});
    }
}
