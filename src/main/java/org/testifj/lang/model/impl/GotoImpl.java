package org.testifj.lang.model.impl;

import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Goto;

public final class GotoImpl extends AbstractElement implements Goto {

    private final int programCounter;

    private final int relativeOffset;

    public GotoImpl(int programCounter, int relativeOffset) {
        assert programCounter >= 0 : "Program counter must be positive";

        this.programCounter = programCounter;
        this.relativeOffset = relativeOffset;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    @Override
    public int getRelativeOffset() {
        return relativeOffset;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.GOTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GotoImpl aGoto = (GotoImpl) o;

        if (programCounter != aGoto.programCounter) return false;
        if (relativeOffset != aGoto.relativeOffset) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = programCounter;
        result = 31 * result + relativeOffset;
        return result;
    }

    @Override
    public String toString() {
        return "GotoImpl{" +
                "programCounter=" + programCounter +
                ", relativeOffset=" + relativeOffset +
                '}';
    }
}
