package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.Procedure;

import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;

public class ProgramCounterTest {

    @Test
    public void defaultConstructorShouldInitializePCToZero() {
        expect(new ProgramCounterImpl().get()).toBe(0);
    }

    @Test
    public void programCounterShouldRetainPC() {
        final ProgramCounter pc = new ProgramCounterImpl(1234);

        expect(pc.get()).toBe(1234);
    }

    @Test
    public void advanceShouldIncreaseProgramCounterValue() {
        final ProgramCounter programCounter = new ProgramCounterImpl();

        programCounter.advance();

        expect(programCounter.get()).toBe(1);
    }

    @Test
    public void lookAheadProcedureShouldBeCalledWhenPCReachesValue() throws Exception {
        final Procedure procedure1 = mock(Procedure.class);
        final Procedure procedure2 = mock(Procedure.class);
        final Procedure procedure3 = mock(Procedure.class);
        final Procedure procedure4 = mock(Procedure.class);

        final ProgramCounter programCounter = new ProgramCounterImpl();

        programCounter.lookAhead(1, procedure1);
        programCounter.lookAhead(1, procedure2);
        programCounter.lookAhead(3, procedure3);
        programCounter.lookAhead(4, procedure4);

        programCounter.advance();

        verify(procedure1).call();
        verify(procedure2).call();
        verifyZeroInteractions(procedure3, procedure4);

        programCounter.advance();

        verifyNoMoreInteractions(procedure1, procedure2);
        verifyZeroInteractions(procedure3, procedure4);

        programCounter.advance();
        verifyNoMoreInteractions(procedure1, procedure2);
        verify(procedure3).call();
        verifyZeroInteractions(procedure4);

        programCounter.advance();

        verifyNoMoreInteractions(procedure1, procedure2, procedure3);
        verify(procedure4).call();
    }

    @Test
    public void lookAheadShouldFailIfProvidedPCIsLessThanOrEqualToCurrentPC() {
        final ProgramCounter pc = new ProgramCounterImpl(1);

        expect(() -> pc.lookAhead(0, mock(Procedure.class))).toThrow(AssertionError.class);
        expect(() -> pc.lookAhead(1, mock(Procedure.class))).toThrow(AssertionError.class);
    }

    @Test
    public void lookAheadShouldNotAcceptNullProcedure() {
        final ProgramCounter pc = new ProgramCounterImpl();

        expect(() -> pc.lookAhead(1, null)).toThrow(AssertionError.class);
    }

}
