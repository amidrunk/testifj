package org.testifj.lang.model;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;

public class MergedElementMetaDataTest {

    private final ElementMetaData candidate1 = mock(ElementMetaData.class, "candidate1");

    private final ElementMetaData candidate2 = mock(ElementMetaData.class, "candidate2");

    private final MergedElementMetaData mergedElementMetaData = new MergedElementMetaData(candidate1, candidate2);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new MergedElementMetaData(null, candidate2)).toThrow(AssertionError.class);
        expect(() -> new MergedElementMetaData(candidate1, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldReturnCandidates() {
        expect(mergedElementMetaData.getFirstCandidate()).toBe(candidate1);
        expect(mergedElementMetaData.getSecondCandidate()).toBe(candidate2);
    }

    @Test
    public void propertiesShouldBeReturnedFromFirstCandidateIfAvailable() {
        when(candidate1.hasLineNumber()).thenReturn(true);
        when(candidate1.getLineNumber()).thenReturn(1234);
        when(candidate1.hasProgramCounter()).thenReturn(true);
        when(candidate1.getProgramCounter()).thenReturn(2345);

        expect(mergedElementMetaData.hasLineNumber()).toBe(true);
        expect(mergedElementMetaData.getLineNumber()).toBe(1234);
        expect(mergedElementMetaData.hasProgramCounter()).toBe(true);
        expect(mergedElementMetaData.getProgramCounter()).toBe(2345);

        verifyZeroInteractions(candidate2);
    }

    @Test
    public void propertiesShouldBeReturnedFromSecondCandidateIfNotAvailableInFirst() {
        when(candidate1.hasLineNumber()).thenReturn(false);
        when(candidate1.hasProgramCounter()).thenReturn(false);
        when(candidate2.hasLineNumber()).thenReturn(true);
        when(candidate2.getLineNumber()).thenReturn(1234);
        when(candidate2.hasProgramCounter()).thenReturn(true);
        when(candidate2.getProgramCounter()).thenReturn(2345);

        expect(mergedElementMetaData.hasLineNumber()).toBe(true);
        expect(mergedElementMetaData.getLineNumber()).toBe(1234);
        expect(mergedElementMetaData.hasProgramCounter()).toBe(true);
        expect(mergedElementMetaData.getProgramCounter()).toBe(2345);

        verify(candidate2).getLineNumber();
        verify(candidate2).getProgramCounter();
    }

    @Test
    public void propertiesShouldNotBeAvailableIfNotAvailableInAnyCandidate() {
        when(candidate1.hasLineNumber()).thenReturn(false);
        when(candidate1.hasProgramCounter()).thenReturn(false);
        when(candidate2.hasLineNumber()).thenReturn(false);
        when(candidate2.hasProgramCounter()).thenReturn(false);

        expect(mergedElementMetaData.hasLineNumber()).toBe(false);
        expect(() -> mergedElementMetaData.getLineNumber()).toThrow(IllegalStateException.class);
        expect(mergedElementMetaData.hasProgramCounter()).toBe(false);
        expect(() -> mergedElementMetaData.getProgramCounter()).toThrow(IllegalStateException.class);
    }

}