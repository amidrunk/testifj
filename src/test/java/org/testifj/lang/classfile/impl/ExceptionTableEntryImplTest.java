package org.testifj.lang.classfile.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.classfile.impl.ExceptionTableEntryImpl;

import java.lang.reflect.Type;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ExceptionTableEntryImplTest {

    @Test
    public void constructorShouldRetainParameters() {
        final Type catchType = Mockito.mock(Type.class);
        final ExceptionTableEntryImpl entry = new ExceptionTableEntryImpl(1, 2, 3, catchType);

        expect(entry.getStartPC()).toBe(1);
        expect(entry.getEndPC()).toBe(2);
        expect(entry.getHandlerPC()).toBe(3);
        expect(entry.getCatchType()).toBe(catchType);
    }

    @Test
    public void tableEntryRepresentingFinallyClauseCanBeCreated() {
        final ExceptionTableEntryImpl finallyEntry = new ExceptionTableEntryImpl(0, 1, 2, null);

        expect(finallyEntry.getCatchType()).toBe(equalTo(null));
        expect(finallyEntry.getStartPC()).toBe(0);
        expect(finallyEntry.getEndPC()).toBe(1);
        expect(finallyEntry.getHandlerPC()).toBe(2);
    }

}
