package org.testifj.lang.impl;

import org.testifj.lang.LocalVariable;
import org.testifj.lang.LocalVariableTable;

import java.util.Arrays;
import java.util.List;

public final class LocalVariableTableImpl implements LocalVariableTable {

    private final LocalVariable[] localVariables;

    public LocalVariableTableImpl(LocalVariable[] localVariables) {
        assert localVariables != null : "Local variables can't be null";

        this.localVariables = localVariables;
    }

    @Override
    public List<LocalVariable> getLocalVariables() {
        return Arrays.asList(localVariables);
    }
}
