package org.testifj.lang.classfile.impl;

import org.testifj.lang.classfile.LocalVariable;
import org.testifj.lang.classfile.LocalVariableTable;

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
