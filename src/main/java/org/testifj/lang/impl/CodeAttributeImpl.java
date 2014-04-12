package org.testifj.lang.impl;

import org.testifj.lang.CodeAttribute;

public final class CodeAttributeImpl implements CodeAttribute {

    private final byte[] byteCode;

    public CodeAttributeImpl(byte[] byteCode) {
        assert byteCode != null : "Byte code can't be null";

        this.byteCode = byteCode;
    }

    @Override
    public byte[] getData() {
        return byteCode;
    }

}
