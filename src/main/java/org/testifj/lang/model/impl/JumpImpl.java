package org.testifj.lang.model.impl;

import org.testifj.lang.model.Jump;

public final class JumpImpl implements Jump {

    private final int targetPC;

    public JumpImpl(int targetPC) {
        assert targetPC >= 0 : "Target PC must be positive";
        this.targetPC = targetPC;
    }

    @Override
    public int getTargetPC() {
        return targetPC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JumpImpl jump = (JumpImpl) o;

        if (targetPC != jump.targetPC) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return targetPC;
    }

    @Override
    public String toString() {
        return "JumpImpl{" +
                "targetPC=" + targetPC +
                '}';
    }
}
