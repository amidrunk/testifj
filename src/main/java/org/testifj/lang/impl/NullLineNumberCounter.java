package org.testifj.lang.impl;

import org.testifj.lang.LineNumberCounter;

public final class NullLineNumberCounter implements LineNumberCounter {
    @Override
    public int get() {
        return -1;
    }
}
