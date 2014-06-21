package org.testifj.lang.decompile.impl;

import org.testifj.lang.decompile.LineNumberCounter;
import org.testifj.lang.classfile.LineNumberTable;
import org.testifj.lang.classfile.LineNumberTableEntry;
import org.testifj.lang.decompile.impl.ProgramCounter;

public final class LineNumberCounterImpl implements LineNumberCounter {

    private final ProgramCounter programCounter;

    private final LineNumberTable lineNumberTable;

    private LineNumberTableEntry[] entries;

    private int currentLineNumberTableEntryIndex = -1;

    public LineNumberCounterImpl(ProgramCounter programCounter, LineNumberTable lineNumberTable) {
        assert programCounter != null : "Program counter can't be null";
        assert lineNumberTable != null : "Line number table can't be null";

        this.programCounter = programCounter;
        this.lineNumberTable = lineNumberTable;
    }

    @Override
    public int get() {
        final int pc = programCounter.get();

        if (currentLineNumberTableEntryIndex == -1) {
            this.entries = lineNumberTable.getEntries().stream().toArray(LineNumberTableEntry[]::new);
            this.currentLineNumberTableEntryIndex = 0;
        }

        if (currentLineNumberTableEntryIndex < entries.length - 1) {
            if (pc >= entries[currentLineNumberTableEntryIndex + 1].getStartPC()) {
                currentLineNumberTableEntryIndex++;
            }
        }

        return entries[currentLineNumberTableEntryIndex].getLineNumber();
    }
}
