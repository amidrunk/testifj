package org.testifj.lang;

import org.testifj.lang.impl.ProgramCounter;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class InputStreamCodeStream implements CodeStream {

    public static final int PEEK_LIMIT = 128;

    private final DataInputStream inputStream;

    private final ProgramCounter programCounter;

    private boolean peeking = false;

    private int peekCount = -1;

    public InputStreamCodeStream(InputStream inputStream, ProgramCounter programCounter) {
        assert inputStream != null : "Input stream can't be null";
        assert programCounter != null : "Program counter can't be null";

        this.inputStream = new DataInputStream(new BufferedInputStream(inputStream));
        this.programCounter = programCounter;
    }

    @Override
    public int nextInstruction() throws IOException {
        unpeek();

        final int instruction = inputStream.readUnsignedByte();

        programCounter.advance();

        return instruction;
    }

    @Override
    public int peekInstruction() throws IOException {
        peek();
        peekCount += 1;
        return inputStream.readUnsignedByte();
    }

    @Override
    public int nextByte() throws IOException {
        unpeek();

        final byte nextByte = inputStream.readByte();

        programCounter.advance();

        return nextByte;
    }

    @Override
    public int peekByte() throws IOException {
        peek();
        peekCount += 1;
        return inputStream.readByte();
    }

    @Override
    public int nextUnsignedByte() throws IOException {
        unpeek();

        final int nextByte = inputStream.readUnsignedByte();

        programCounter.advance();

        return nextByte;
    }

    @Override
    public int peekUnsignedByte() throws IOException {
        peek();
        peekCount += 1;
        return inputStream.readUnsignedByte();
    }

    @Override
    public int peekUnsignedShort() throws IOException {
        peek();
        peekCount += 2;
        return inputStream.readUnsignedShort();
    }

    @Override
    public int nextUnsignedShort() throws IOException {
        unpeek();

        final int nextUnsignedShort = inputStream.readUnsignedShort();

        programCounter.advance();
        programCounter.advance();

        return nextUnsignedShort;
    }

    @Override
    public void commit() {
        if (peeking) {
            peeking = false;

            for (int i = 0; i < peekCount; i++) {
                programCounter.advance();
            }
        }
    }

    private void peek() {
        if (!peeking) {
            inputStream.mark(PEEK_LIMIT);
            peeking = true;
            peekCount = 0;
        }
    }

    private void unpeek() throws IOException {
        if (peeking) {
            peeking = false;
            peekCount = -1;

            inputStream.reset();
        }
    }
}
