package org.testifj.lang.impl;

import org.testifj.Procedure;

public interface ProgramCounter {

    void lookAhead(int targetPc, Procedure procedure);

    void advance();

    int get();

}
