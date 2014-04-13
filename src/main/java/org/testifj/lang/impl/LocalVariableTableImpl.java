package org.testifj.lang.impl;

import org.testifj.io.ByteBufferInputStream;
import org.testifj.lang.LocalVariable;
import org.testifj.lang.LocalVariableTable;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public final class LocalVariableTableImpl implements LocalVariableTable {

    private final ByteBuffer data;

    private final LocalVariable[] localVariables;

    public LocalVariableTableImpl(ByteBuffer data, LocalVariable[] localVariables) {
        assert data != null : "Data can't be null";
        assert localVariables != null : "Local variables can't be null";

        this.data = data;
        this.localVariables = localVariables;
    }

    @Override
    public List<LocalVariable> getLocalVariables() {
        return Arrays.asList(localVariables);
    }

    @Override
    public InputStream getData() {
        return new ByteBufferInputStream(data);
    }
}
