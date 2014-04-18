package org.testifj.lang.impl;

import org.testifj.lang.Procedure;

public interface ProgramCounter {
    void lookAhead(int targetPc, Procedure procedure);

    void advance();

    int get();
}
