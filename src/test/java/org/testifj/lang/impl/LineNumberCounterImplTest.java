package org.testifj.lang.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.LineNumberTable;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;

public class LineNumberCounterImplTest {

    private final LineNumberTable lineNumberTable = mock(LineNumberTable.class);

    private final ProgramCounter programCounter = mock(ProgramCounter.class);

    private final LineNumberCounterImpl lineNumberCounter = new LineNumberCounterImpl(programCounter, lineNumberTable);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new LineNumberCounterImpl(null, lineNumberTable)).toThrow(AssertionError.class);
        expect(() -> new LineNumberCounterImpl(programCounter, null)).toThrow(AssertionError.class);
    }

    @Test
    public void getLineNumberShouldResolveLineNumberFromLineNumberTable() {
        when(lineNumberTable.getEntries()).thenReturn(Arrays.asList(
                new LineNumberTableEntryImpl(0, 1),
                new LineNumberTableEntryImpl(3, 2),
                new LineNumberTableEntryImpl(5, 3)
        ));

        when(programCounter.get()).thenReturn(0, 1, 2, 3, 4, 5, 6, 7, 8);

        expect(lineNumberCounter.get()).toBe(1);
        expect(lineNumberCounter.get()).toBe(1);
        expect(lineNumberCounter.get()).toBe(1);
        expect(lineNumberCounter.get()).toBe(2);
        expect(lineNumberCounter.get()).toBe(2);
        expect(lineNumberCounter.get()).toBe(3);
        expect(lineNumberCounter.get()).toBe(3);
        expect(lineNumberCounter.get()).toBe(3);
        expect(lineNumberCounter.get()).toBe(3);
    }

}
