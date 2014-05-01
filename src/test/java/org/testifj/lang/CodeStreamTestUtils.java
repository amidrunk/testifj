package org.testifj.lang;

import org.testifj.io.ByteBufferInputStream;
import org.testifj.lang.impl.InputStreamCodeStream;
import org.testifj.lang.impl.ProgramCounterImpl;

import java.nio.ByteBuffer;

public final class CodeStreamTestUtils {

    @SafeVarargs
    public static CodeStream codeStream(int ... buffer) {
        final byte[] bytes = new byte[buffer.length];

        for (int i = 0; i < buffer.length; i++) {
            assert (buffer[i] & ~0xFF) == 0;
            bytes[i] = (byte) buffer[i];
        }

        return codeStream(bytes, 0, bytes.length);
    }

    public static CodeStream codeStream(byte[] buffer, int offset, int length) {
        return new InputStreamCodeStream(new ByteBufferInputStream(ByteBuffer.wrap(buffer, offset, length)));
    }

}
