package org.testifj.lang.impl;

import org.testifj.io.ByteBufferInputStream;
import org.testifj.lang.LineNumberTable;
import org.testifj.lang.LineNumberTableEntry;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public final class LineNumberTableImpl implements LineNumberTable {

    private final ByteBuffer data;

    private final LineNumberTableEntry[] entries;

    public LineNumberTableImpl(ByteBuffer data, LineNumberTableEntry[] entries) {
        assert data != null : "Data can't be null";
        assert entries != null : "Entries can't be null";

        this.data = data;
        this.entries = entries;
    }

    @Override
    public List<LineNumberTableEntry> getEntries() {
        return Arrays.asList(entries);
    }

    @Override
    public InputStream getData() {
        return new ByteBufferInputStream(data);
    }
}
