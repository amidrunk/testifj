package org.testifj.lang;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.impl.*;
import org.testifj.matchers.core.OptionalThatIs;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class MethodsTest {

    private final ClassFile classFile = Mockito.mock(ClassFile.class);

    private final Method methodWithoutLineNumbers = mock(Method.class, "methodWithoutLineNumbers");
    private final Method methodWithLineNumbers1 = mock(Method.class, "methodWithLineNumbers1");
    private final Method methodWithLineNumbers2 = mock(Method.class, "methodWithLineNumbers2");

    @Before
    public void setup() {
        when(methodWithoutLineNumbers.getLineNumberTable()).thenReturn(Optional.<LineNumberTable>empty());

        when(methodWithLineNumbers1.getLineNumberTable()).thenReturn(Optional.of(new LineNumberTableImpl(new LineNumberTableEntry[0], new Range(10, 13))));
        when(methodWithLineNumbers2.getLineNumberTable()).thenReturn(Optional.of(new LineNumberTableImpl(new LineNumberTableEntry[0], new Range(20, 22))));

        when(classFile.getMethods()).thenReturn(Arrays.asList(
                methodWithoutLineNumbers,
                methodWithLineNumbers1,
                methodWithLineNumbers2));
    }

    @Test
    public void findMethodForLineNumberShouldNotAcceptInvalidArguments() {
        expect(() -> Methods.findMethodForLineNumber(null, 123)).toThrow(AssertionError.class);
        expect(() -> Methods.findMethodForLineNumber(classFile, -1)).toThrow(AssertionError.class);
    }

    @Test
    public void findMethodsForLineNumberShouldReturnNonPresentOptionalIfMethodIsNotFound() {
        when(classFile.getMethods()).thenReturn(Arrays.asList(
                methodWithoutLineNumbers,
                methodWithLineNumbers1,
                methodWithLineNumbers2
        ));

        expect(Methods.findMethodForLineNumber(classFile, 123)).not().toBe(present());
    }

    @Test
    public void findMethodsForLineNumberShouldReturnMatchingMethod() {
        expect(Methods.findMethodForLineNumber(classFile, 10)).toBe(optionalOf(methodWithLineNumbers1));
        expect(Methods.findMethodForLineNumber(classFile, 11)).toBe(optionalOf(methodWithLineNumbers1));
        expect(Methods.findMethodForLineNumber(classFile, 12)).toBe(optionalOf(methodWithLineNumbers1));
        expect(Methods.findMethodForLineNumber(classFile, 13)).toBe(optionalOf(methodWithLineNumbers1));

        expect(Methods.findMethodForLineNumber(classFile, 20)).toBe(optionalOf(methodWithLineNumbers2));
        expect(Methods.findMethodForLineNumber(classFile, 21)).toBe(optionalOf(methodWithLineNumbers2));
        expect(Methods.findMethodForLineNumber(classFile, 22)).toBe(optionalOf(methodWithLineNumbers2));
    }

    @Test
    public void getExceptionTableEntryForCatchLocationShouldNotAcceptInvalidArguments() {
        expect(() -> Methods.getExceptionTableEntryForCatchLocation(null, 123)).toThrow(AssertionError.class);
        expect(() -> Methods.getExceptionTableEntryForCatchLocation(methodWithLineNumbers1, -1)).toThrow(AssertionError.class);
    }

    @Test
    public void getExceptionTableEntryForCatchLocationShouldReturnNonPresentOptionalIfNoExceptionTableExists() {
        when(methodWithLineNumbers1.getCode()).thenReturn(mock(CodeAttribute.class));

        expect(Methods.getExceptionTableEntryForCatchLocation(methodWithLineNumbers1, 1234)).not().toBe(present());
    }

    @Test
    public void getExceptionTableEntryForCatchLocationShouldReturnNonPresentOptionalForNonCatchLocation() {
        final CodeAttribute codeAttribute = mock(CodeAttribute.class);

        when(codeAttribute.getExceptionTable()).thenReturn(Arrays.asList(new ExceptionTableEntryImpl(0, 10, 15, Exception.class)));
        when(methodWithLineNumbers1.getCode()).thenReturn(codeAttribute);

        expect(Methods.getExceptionTableEntryForCatchLocation(methodWithLineNumbers1, 9)).not().toBe(present());
    }

    @Test
    public void getExceptionTableEntryForCatchLocationShouldReturnMatchingEntry() {
        final CodeAttribute codeAttribute = mock(CodeAttribute.class);
        final ExceptionTableEntry expectedEntry = new ExceptionTableEntryImpl(0, 10, 15, Exception.class);

        when(codeAttribute.getExceptionTable()).thenReturn(Arrays.asList(expectedEntry));
        when(methodWithLineNumbers1.getCode()).thenReturn(codeAttribute);

        expect(Methods.getExceptionTableEntryForCatchLocation(methodWithLineNumbers1, 10)).toBe(optionalOf(expectedEntry));
    }

    @Test
    public void findLocalVariableForIndexAndPCShouldNotAcceptInvalidArguments() {
        expect(() -> Methods.findLocalVariableForIndexAndPC(null, 0, 0)).toThrow(AssertionError.class);
        expect(() -> Methods.findLocalVariableForIndexAndPC(mock(Method.class), -1, 0)).toThrow(AssertionError.class);
        expect(() -> Methods.findLocalVariableForIndexAndPC(mock(Method.class), 0, -1)).toThrow(AssertionError.class);
    }

    @Test
    public void findLocalVariableForIndexAndPCShouldReturnNonPresentOptionalIfNoVariableTableExists() {
        when(methodWithLineNumbers1.getLocalVariableTable()).thenReturn(Optional.empty());

        final Optional<LocalVariable> optionalLocal = Methods.findLocalVariableForIndexAndPC(methodWithLineNumbers1, 0, 0);

        expect(optionalLocal).not().toBe(present());
    }

    @Test
    public void findLocalVariableShouldReturnMatchingVariable() {
        final LocalVariableImpl variable1 = new LocalVariableImpl(0, 10, "foo", String.class, 0);
        final LocalVariableImpl variable2 = new LocalVariableImpl(11, 20, "bar", int.class, 0);

        when(methodWithLineNumbers1.getLocalVariableTable()).thenReturn(Optional.of(new LocalVariableTableImpl(new LocalVariable[]{
                variable1,
                variable2
        })));

        expect(Methods.findLocalVariableForIndexAndPC(methodWithLineNumbers1, 0, 0)).toBe(optionalOf(variable1));
        expect(Methods.findLocalVariableForIndexAndPC(methodWithLineNumbers1, 0, 11)).toBe(optionalOf(variable2));
    }
}
