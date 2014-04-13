package org.testifj.io;

import java.io.InputStream;
import java.nio.ByteBuffer;

public final class ByteBufferInputStream extends InputStream {

    private final ByteBuffer byteBuffer;

    public ByteBufferInputStream(ByteBuffer byteBuffer) {
        assert byteBuffer != null : "Byte buffer can't be null";
        this.byteBuffer = byteBuffer;
    }

    @Override
    public int read() {
        if (byteBuffer.remaining() == 0) {
            return -1;
        }

        return byteBuffer.get();
    }

    @Override
    public int available() {
        return byteBuffer.remaining();
    }
}
