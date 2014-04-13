package org.testifj.lang.impl;

import org.testifj.io.ByteBufferInputStream;
import org.testifj.lang.CodeAttribute;

import java.io.InputStream;
import java.nio.ByteBuffer;

public final class CodeAttributeImpl implements CodeAttribute {

    private final ByteBuffer data;

    private final int maxStack;

    private final int maxLocals;

    private final ByteBuffer byteCode;

    public CodeAttributeImpl(ByteBuffer data, int maxStack, int maxLocals, ByteBuffer byteCode) {
        assert data != null : "Attribute data can't be null";
        assert maxStack >= 0 : "Max-stack must be positive";
        assert maxLocals >= 0 : "Max-locals must be positive";
        assert byteCode != null : "Byte code can't be null";

        this.data = data.asReadOnlyBuffer();
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.byteCode = byteCode.asReadOnlyBuffer();
    }

    @Override
    public InputStream getData() {
        return new ByteBufferInputStream(data.asReadOnlyBuffer());
    }

    @Override
    public int getMaxStack() {
        return maxStack;
    }

    @Override
    public int getMaxLocals() {
        return maxLocals;
    }

    @Override
    public InputStream getCode() {
        return new ByteBufferInputStream(byteCode.asReadOnlyBuffer());
    }
}
