package org.testifj.lang.impl;

import org.testifj.lang.LineNumberTable;
import org.testifj.lang.LineNumberTableEntry;

import java.util.Arrays;
import java.util.List;

public final class LineNumberTableImpl implements LineNumberTable {

    private final LineNumberTableEntry[] entries;

    public LineNumberTableImpl(LineNumberTableEntry[] entries) {
        assert entries != null : "Entries can't be null";

        this.entries = entries;
    }

    @Override
    public List<LineNumberTableEntry> getEntries() {
        return Arrays.asList(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineNumberTableImpl that = (LineNumberTableImpl) o;

        if (!Arrays.equals(entries, that.entries)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(entries);
    }

    @Override
    public String toString() {
        return "LineNumberTableImpl{" +
                "entries=" + Arrays.toString(entries) +
                '}';
    }
}
