package org.testifj.io;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class ByteBufferInputStreamTest {

    @Test
    public void constructorShouldNotAcceptNullByteBuffer() {
        expect(() -> new ByteBufferInputStream(null)).toThrow(AssertionError.class);
    }

    @Test
    public void entireByteBufferCanBeRead() throws Exception {
        final byte[] originalArray = {1, 2, 3, 4};
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(originalArray));

        expect(IOUtils.toByteArray(in)).toBe(originalArray);
    }

    @Test
    public void availableShouldReturnRemainingNumberOfBytes() {
        final byte[] originalArray = {1, 2, 3, 4};
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(originalArray));

        expect(in.available()).toBe(4);
        expect(in.read()).toBe(1);
        expect(in.available()).toBe(3);
        expect(in.read()).toBe(2);
    }

    @Test
    public void skipShouldSkipBytes() throws IOException {
        final byte[] originalArray = {1, 2, 3, 4, 5};
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(originalArray));

        expect(in.skip(1)).toBe(1L);
        expect(in.available()).toBe(4);
        expect(in.read()).toBe(2);
        expect(in.available()).toBe(3);
        expect(in.skip(2)).toBe(2L);
        expect(in.available()).toBe(1);
        expect(in.read()).toBe(5);
        expect(in.skip(10)).toBe(0L);
        expect(in.available()).toBe(0);
    }

    @Test
    public void readBufferShouldNotAcceptInvalidParameters() throws Exception {
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(new byte[]{1, 2}));

        expect(() -> in.read(null, 0, 1)).toThrow(AssertionError.class);
        expect(() -> in.read(new byte[1], -1, 1)).toThrow(AssertionError.class);
        expect(() -> in.read(new byte[1], 0, -1)).toThrow(AssertionError.class);
    }

}
