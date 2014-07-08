package org.testifj.lang.decompile;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.model.Statement;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.lang.decompile.DecompilationContextQueries.lastStatement;
import static org.testifj.lang.model.Sequences.emptySequence;
import static org.testifj.lang.model.Sequences.sequenceOf;

public class DecompilerDelegatesTest {

    private final DecompilerTransformation transformation = mock(DecompilerTransformation.class);
    private final DecompilationContext context = mock(DecompilationContext.class);
    private final CodeStream codeStream = mock(CodeStream.class);

    @Test
    public void forQueryShouldNotAcceptNullModelQuery() {
        expect(() -> DecompilerDelegates.forQuery(null)).toThrow(AssertionError.class);
    }

    @Test
    public void forQueryShouldNotAcceptNullTransformation() {
        expect(() -> DecompilerDelegates.forQuery(lastStatement()).apply(null)).toThrow(AssertionError.class);
    }

    @Test
    public void forQueryShouldApplyTransformationIfQueryMatches() throws IOException {
        final DecompilerDelegate delegate = DecompilerDelegates.forQuery(lastStatement()).apply(transformation);
        final Statement statement = mock(Statement.class);

        when(context.getStatements()).thenReturn(sequenceOf(statement));

        delegate.apply(context, codeStream, ByteCode.nop);

        verify(transformation).apply(eq(context), eq(codeStream), eq(ByteCode.nop), eq(statement));
    }

    @Test
    public void forQueryShouldNotApplyTransformationIfQueryDoesNotMatch() throws IOException {
        final DecompilerDelegate delegate = DecompilerDelegates.forQuery(lastStatement()).apply(transformation);

        when(context.getStatements()).thenReturn(emptySequence());

        delegate.apply(context, codeStream, ByteCode.nop);

        verifyZeroInteractions(transformation);
    }

}