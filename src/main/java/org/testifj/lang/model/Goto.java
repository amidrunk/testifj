package org.testifj.lang.model;

public interface Goto extends Statement {

    int getProgramCounter();

    int getRelativeOffset();

}