package org.testifj.lang;

import org.junit.Test;
import org.testifj.lang.impl.ProgramCounter;
import org.testifj.lang.impl.ProgramCounterImpl;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class InputStreamCodeStreamTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new InputStreamCodeStream(null, new ProgramCounterImpl())).toThrow(AssertionError.class);
        expect(() -> new InputStreamCodeStream(mock(InputStream.class), null)).toThrow(AssertionError.class);
    }

    @Test
    public void nextInstructionShouldReturnNextByteInStream() {
        given(new InputStreamCodeStream(in(1, 2, 3), new ProgramCounterImpl())).then(in -> {
            expect(in.nextInstruction()).toBe(1);
            expect(in.nextInstruction()).toBe(2);
            expect(in.nextInstruction()).toBe(3);
        });
    }

    @Test
    public void nextByteShouldReturnNextByteInStream() {
        given(new InputStreamCodeStream(in(-1, 0, 1), new ProgramCounterImpl())).then(in -> {
            expect(in.nextByte()).toBe(-1);
            expect(in.nextByte()).toBe(0);
            expect(in.nextByte()).toBe(1);
        });
    }

    @Test
    public void allNextMethodsShouldFailIfEOFHasBeenReached() {
        given(new InputStreamCodeStream(in(), new ProgramCounterImpl())).then(in -> {
            expect(in::nextByte).toThrow(EOFException.class);
            expect(in::nextInstruction).toThrow(EOFException.class);
            expect(in::nextUnsignedShort).toThrow(EOFException.class);
        });
    }

    @Test
    public void nextMethodsShouldAdvancePC() {
        final ProgramCounter pc = new ProgramCounterImpl(-1);
        final InputStreamCodeStream stream = new InputStreamCodeStream(in(1, 2, 3, 4, 5), pc);

        given(stream).when(CodeStream::nextByte).then(() -> expect(pc.get()).toBe(0));
        given(stream).when(CodeStream::nextInstruction).then(() -> expect(pc.get()).toBe(1));
        given(stream).when(CodeStream::nextUnsignedShort).then(() -> expect(pc.get()).toBe(3));
    }

    @Test
    public void peekMethodsShouldReturnNextDataInStreamWithoutAdvancingPC() throws IOException {
        final ProgramCounter pc = new ProgramCounterImpl();
        final InputStreamCodeStream in = new InputStreamCodeStream(in(1, 2, 3, 4, 5), pc);

        expect(in.peekInstruction()).toBe(1);
        expect(in.peekByte()).toBe(2);
        expect(in.peekUnsignedShort()).toBe(3 << 8 | 4);
        expect(pc.get()).toBe(0);
    }

    @Test
    public void commitShouldDiscardPeekBufferAndAdvancePC() throws Exception {
        final ProgramCounter pc = mock(ProgramCounter.class);
        final CodeStream cs = new InputStreamCodeStream(in(1, 2, 3, 4, 5), pc);

        expect(cs.peekInstruction()).toBe(1);
        expect(cs.peekByte()).toBe(2);
        expect(cs.peekUnsignedShort()).toBe(3 << 8 | 4);

        cs.commit();

        verify(pc, times(4)).advance();

        expect(cs.nextInstruction()).toBe(5);
    }

    @Test
    public void readMethodsShouldResetPeek() throws IOException {
        final ProgramCounter pc = mock(ProgramCounter.class);
        final InputStreamCodeStream stream = new InputStreamCodeStream(in(1, 2, 3, 4), pc);

        expect(stream.peekInstruction()).toBe(1);
        expect(stream.nextInstruction()).toBe(1);
        verify(pc, times(1)).advance();

        expect(stream.peekByte()).toBe(2);
        expect(stream.nextByte()).toBe(2);
        verify(pc, times(2)).advance();

        expect(stream.peekUnsignedShort()).toBe(3 << 8 | 4);
        expect(stream.nextUnsignedShort()).toBe(3 << 8 | 4);
        verify(pc, times(4)).advance();
    }

    private InputStream in(int ... data) {
        final byte[] buf = new byte[data.length];

        for (int i = 0;i < data.length; i++) {
            buf[i] = (byte) data[i];
        }

        return new ByteArrayInputStream(buf);
    }
}
