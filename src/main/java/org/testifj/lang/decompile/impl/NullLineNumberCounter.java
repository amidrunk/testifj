package org.testifj.lang.decompile.impl;

import org.testifj.lang.decompile.LineNumberCounter;

public final class NullLineNumberCounter implements LineNumberCounter {
    @Override
    public int get() {
        return -1;
    }
}
