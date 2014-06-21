package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.TypeResolver;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.Decompiler;
import org.testifj.lang.decompile.LineNumberCounter;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Statement;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.CollectionThatIs.empty;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class StackInstructionsTest {

    private final StackInstructions stackInstructions = new StackInstructions();
    private final DecompilationContext decompilationContext = new DecompilationContextImpl(mock(Decompiler.class), mock(Method.class), mock(ProgramCounter.class), mock(LineNumberCounter.class), mock(TypeResolver.class));
    private final CodeStream codeStream = mock(CodeStream.class);
    private final Expression element1 = mock(Expression.class, withSettings().extraInterfaces(Statement.class).name("element1"));
    private final Expression element2 = mock(Expression.class, withSettings().extraInterfaces(Statement.class).name("element2"));
    private final Expression element3 = mock(Expression.class, withSettings().extraInterfaces(Statement.class).name("element3"));
    private final Expression element4 = mock(Expression.class, withSettings().extraInterfaces(Statement.class).name("element4"));

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> stackInstructions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForStackRelatedInstructions() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.pop)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.pop2)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.dup)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.dup_x1)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.dup_x2)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.dup2)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.dup2_x1)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.dup2_x2)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.swap)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void popShouldFailIfStackIsEmpty() throws IOException {
        expect(() -> execute(ByteCode.pop)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void popShouldReduceStack() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        decompilationContext.push(element1);

        execute(ByteCode.pop);

        expect(decompilationContext.getStackedExpressions()).toBe(empty());
        expect(decompilationContext.getStatements()).toBe(collectionOf(element1));
    }

    @Test
    public void popShouldFailForInvalidTypeOnStack() throws IOException {
        when(element1.getType()).thenReturn(long.class);
        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.pop)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void pop2ShouldReduceTwiceForComputationalCategories1() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element1);
        decompilationContext.push(element2);

        execute(ByteCode.pop2);

        expect(decompilationContext.getStackedExpressions()).toBe(empty());
        expect(decompilationContext.getStatements()).toBe(collectionOf(element1, element2));
    }

    @Test
    public void pop2ShouldFailIfLastElementIsComputationalCategory1ButNotSecondLast() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(long.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.pop2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void pop2ShouldFailIfStackIsEmpty() throws IOException {
        expect(() -> execute(ByteCode.pop2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void pop2ShouldPopSingleElementIfElementIsOfComputationalType2() throws IOException {
        when(element1.getType()).thenReturn(long.class);

        decompilationContext.push(element1);

        execute(ByteCode.pop2);

        expect(decompilationContext.getStackedExpressions()).toBe(empty());
        expect(decompilationContext.getStatements()).toBe(collectionOf(element1));
    }

    @Test
    public void dupShouldDuplicateStackedElement() throws IOException {
        when(element1.getType()).thenReturn(int.class);

        decompilationContext.push(element1);

        execute(ByteCode.dup);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element1));
    }

    @Test
    public void dupShouldFailIfStackIsEmpty() throws IOException {
        expect(() -> execute(ByteCode.dup)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dupShouldFailIfStackedElementIsIncorrect() throws IOException {
        when(element1.getType()).thenReturn(long.class);
        decompilationContext.push(element1);
        expect(() -> execute(ByteCode.dup)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup_x1ShouldInsertTopElement() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup_x1);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element2, element1));
    }

    @Test
    public void dup_x1ShouldFailIfStackIsEmpty() {
        expect(() -> execute(ByteCode.dup_x1)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup_x1ShouldFailIfStackContainsOneElement() {
        when(element1.getType()).thenReturn(int.class);
        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.dup_x1)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup_x2ShouldInsertTopElementForStackWithTwoElements() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(int.class);

        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup_x2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element3, element2, element1));
    }

    @Test
    public void dup_x2ShouldFailIfStackContainsThreeElementsWhereAnyIsIncorrect() {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(long.class);

        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.dup_x2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup_x2ShouldInsertTopElementForStackWithThreeElements() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(long.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup_x2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element2, element1));
    }

    @Test
    public void dup_x2ShouldFailIfStackContainsTwoElementsWhereAnyIsInvalid() {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.dup_x2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup2ShouldDuplicateTopTwoElements() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element2, element1, element2, element1));
    }

    @Test
    public void dup2ShouldFailIfOneElementOfComputationalType1IsStacked() {
        when(element1.getType()).thenReturn(int.class);

        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.dup2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup2ShouldDuplicateElementIfOneElementIsStacked() throws IOException {
        when(element1.getType()).thenReturn(long.class);

        decompilationContext.push(element1);

        execute(ByteCode.dup2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element1));
    }

    @Test
    public void dup2_x1ShouldDuplicateTwoElementsAndInsert() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(int.class);

        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup2_x1);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element2, element1, element3, element2, element1));
    }

    @Test
    public void dup2_x1WithThreeStackedElementsShouldFailIfAnyIsInvalid() {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(long.class);

        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.dup2_x1)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup2_x1ShouldDuplicateOneElementIfStackSizeIsTwo() throws IOException {
        when(element1.getType()).thenReturn(long.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup2_x1);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element2, element1));
    }

    @Test
    public void dup2_x1ShouldFailForTwoStackedElementsWhereAnyIsInvalid() {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.dup2_x1)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithStackSize4ShouldDuplicateTwoElementsAndInsert() throws IOException {
        decompilationContext.push(element4);
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(int.class);
        when(element4.getType()).thenReturn(int.class);

        execute(ByteCode.dup2_x2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element2, element1, element4, element3, element2, element1));
    }

    @Test
    public void dup2_x2WithStackSize4AndInvalidComputationalTypeShouldDuplicateTwoElementsAndInsert() throws IOException {
        decompilationContext.push(element4);
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(long.class);
        when(element3.getType()).thenReturn(int.class);
        when(element4.getType()).thenReturn(int.class);

        expect(() -> execute(ByteCode.dup2_x2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithStackSizeTreeAndComputationalTypes211ShouldDuplicateOneElement() throws IOException {
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element1.getType()).thenReturn(long.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(int.class);

        execute(ByteCode.dup2_x2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element3, element2, element1));
    }

    @Test
    public void dup2_x2WithStackSizeTreeAndComputationalTypes122ShouldDuplicateTwoElements() throws IOException {
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element3.getType()).thenReturn(long.class);
        when(element2.getType()).thenReturn(int.class);
        when(element1.getType()).thenReturn(int.class);

        execute(ByteCode.dup2_x2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element2, element1, element3, element2, element1));
    }

    @Test
    public void dup2_x2WithStackSize2ShouldDuplicateOneElement() throws IOException {
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element2.getType()).thenReturn(long.class);
        when(element1.getType()).thenReturn(long.class);

        execute(ByteCode.dup2_x2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element2, element1));
    }

    @Test
    public void dup2_x2WithStackSize2AndInvalidStackShouldFail() throws IOException {
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element2.getType()).thenReturn(long.class);
        when(element1.getType()).thenReturn(int.class);

        expect(() -> execute(ByteCode.dup2_x2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithStackSizeTreeAndInvalidComputationalTypesShouldFail() throws IOException {
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element3.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element1.getType()).thenReturn(int.class);

        expect(() -> execute(ByteCode.dup2_x2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithEmptyStackShouldFail() throws IOException {
        expect(() -> execute(ByteCode.dup2_x2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithInvalidStackSizeShouldFail() throws IOException {
        decompilationContext.push(element1);
        expect(() -> execute(ByteCode.dup2_x2)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void swapShouldFailIfStackIsEmpty() throws IOException {
        expect(() -> execute(ByteCode.swap)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void swapShouldFailIfStackContains1Value() throws IOException {
        decompilationContext.push(element1);
        expect(() -> execute(ByteCode.swap)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void swapShouldRearrangeTopElements() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.swap);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(element1, element2));
    }

    @Test
    public void swapShouldFailIfStackContainsInvalidElements() {
        when(element1.getType()).thenReturn(long.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        expect(() -> execute(ByteCode.swap)).toThrow(ClassFileFormatException.class);
    }

    private void execute(int byteCode) throws IOException {
        configuration().getDecompilerExtension(decompilationContext, byteCode).apply(decompilationContext, codeStream, byteCode);
    }

    private org.testifj.lang.decompile.DecompilerConfiguration configuration() {
        final DecompilerConfigurationImpl.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();
        stackInstructions.configure(configurationBuilder);
        return configurationBuilder.build();
    }

}