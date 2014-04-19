package org.testifj.lang.model;

public interface Jump extends Statement {

    int getTargetPC();

    default ElementType getElementType() {
        return ElementType.JUMP;
    }

}
