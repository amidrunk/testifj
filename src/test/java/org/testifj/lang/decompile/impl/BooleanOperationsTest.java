package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.CodeStreamTestUtils;
import org.testifj.lang.TypeResolver;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.MethodCall;
import org.testifj.lang.model.impl.MethodSignature;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.AST.local;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;

public class BooleanOperationsTest {

    private final BooleanOperations booleanOperations = new BooleanOperations();

    private final DecompilationContext decompilationContext = new DecompilationContextImpl(mock(Decompiler.class), mock(Method.class), mock(ProgramCounter.class), mock(LineNumberCounter.class), mock(TypeResolver.class));

    @Test
    public void integerConstantAsBooleanShouldBeCoercedToBoolean() throws IOException {
        final MethodCall originalCall = AST.call(Object.class, "setBoolean", MethodSignature.parse("(Z)V"), AST.constant(1));

        decompilationContext.getStack().push(originalCall);

        after(ByteCode.invokeinterface);

        final MethodCall expectedCall = originalCall.withParameters(Arrays.<Expression>asList(AST.constant(true)));

        expect(decompilationContext.getStack()).toBe(iterableOf(expectedCall));
    }

    @Test
    public void integerConstantAssignedToBooleanShouldBeCoercedToBoolean() throws IOException {
        decompilationContext.enlist(AST.set(local("foo", boolean.class, 1)).to(constant(1)));

        after(ByteCode.istore_1);

        expect(decompilationContext.getStatements()).toBe(collectionOf(AST.set(local("foo", boolean.class, 1)).to(constant(true))));
    }

    private void after(int byteCode, int ... code) throws IOException {
        final Iterator<DecompilerDelegate> delegates = configuration().getCorrectionalDecompilerEnhancements(decompilationContext, byteCode);

        while (delegates.hasNext()) {
            delegates.next().apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
        }
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationImpl.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();
        booleanOperations.configure(configurationBuilder);
        return configurationBuilder.build();
    }

}