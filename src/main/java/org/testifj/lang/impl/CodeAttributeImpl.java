package org.testifj.lang.impl;

import org.testifj.io.ByteBufferInputStream;
import org.testifj.lang.Attribute;
import org.testifj.lang.CodeAttribute;
import org.testifj.lang.ExceptionTableEntry;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public final class CodeAttributeImpl implements CodeAttribute {

    private final ByteBuffer data;

    private final int maxStack;

    private final int maxLocals;

    private final ByteBuffer byteCode;

    private final List<ExceptionTableEntry> exceptionTable;

    private final List<Attribute> attributes;

    public CodeAttributeImpl(ByteBuffer data, int maxStack, int maxLocals, ByteBuffer byteCode,
                             List<ExceptionTableEntry> exceptionTable, List<Attribute> attributes) {
        assert data != null : "Attribute data can't be null";
        assert maxStack >= 0 : "Max-stack must be positive";
        assert maxLocals >= 0 : "Max-locals must be positive";
        assert byteCode != null : "Byte code can't be null";
        assert exceptionTable != null : "Exception table can't be null";
        assert attributes != null : "Attributes can't be null";

        this.data = data.asReadOnlyBuffer();
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.byteCode = byteCode.asReadOnlyBuffer();
        this.exceptionTable = exceptionTable;
        this.attributes = attributes;
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

    @Override
    public List<ExceptionTableEntry> getExceptionTable() {
        return Collections.unmodifiableList(exceptionTable);
    }

    @Override
    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }
}