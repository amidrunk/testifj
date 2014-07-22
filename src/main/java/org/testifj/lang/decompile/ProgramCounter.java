package org.testifj.lang.decompile;

import org.testifj.Procedure;

public interface ProgramCounter {

    void lookAhead(int targetPc, Procedure procedure);

    void advance();

    int get();

}
